/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.action.admin.cluster.node.tasks;

import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.ResourceNotFoundException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.FailedNodeException;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.TaskOperationFailure;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthAction;
import org.elasticsearch.action.admin.cluster.node.tasks.cancel.CancelTasksResponse;
import org.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskResponse;
import org.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksAction;
import org.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshAction;
import org.elasticsearch.action.admin.indices.upgrade.post.UpgradeAction;
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryAction;
import org.elasticsearch.action.bulk.BulkAction;
import org.elasticsearch.action.fieldstats.FieldStatsAction;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.tasks.PersistedTaskInfo;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.tasks.TaskInfo;
import org.elasticsearch.tasks.TaskPersistenceService;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.tasks.MockTaskManager;
import org.elasticsearch.test.tasks.MockTaskManagerListener;
import org.elasticsearch.test.transport.MockTransportService;
import org.elasticsearch.transport.ReceiveTimeoutTransportException;
import org.elasticsearch.transport.TransportService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.singleton;
import static org.elasticsearch.common.unit.TimeValue.timeValueMillis;
import static org.elasticsearch.common.unit.TimeValue.timeValueSeconds;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertAcked;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertNoFailures;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertThrows;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;

/**
 * Integration tests for task management API
 * <p>
 * We need at least 2 nodes so we have a master node a non-master node
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, minNumDataNodes = 2)
public class TasksIT extends ESIntegTestCase {

    private Map<Tuple<String, String>, RecordingTaskManagerListener> listeners = new HashMap<>();

    @Override
    protected boolean addMockTransportService() {
        return false;
    }

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return pluginList(MockTransportService.TestPlugin.class, TestTaskPlugin.class);
    }

    @Override
    protected Collection<Class<? extends Plugin>> transportClientPlugins() {
        return nodePlugins();
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return Settings.builder()
            .put(super.nodeSettings(nodeOrdinal))
            .put(MockTaskManager.USE_MOCK_TASK_MANAGER_SETTING.getKey(), true)
            .build();
    }

    public void testTaskCounts() {
        // Run only on data nodes
        ListTasksResponse response = client().admin().cluster().prepareListTasks("data:true").setActions(ListTasksAction.NAME + "[n]")
                .get();
        assertThat(response.getTasks().size(), greaterThanOrEqualTo(cluster().numDataNodes()));
    }

    public void testMasterNodeOperationTasks() {
        registerTaskManageListeners(ClusterHealthAction.NAME);

        // First run the health on the master node - should produce only one task on the master node
        internalCluster().masterClient().admin().cluster().prepareHealth().get();
        assertEquals(1, numberOfEvents(ClusterHealthAction.NAME, Tuple::v1)); // counting only registration events
        assertEquals(1, numberOfEvents(ClusterHealthAction.NAME, event -> event.v1() == false)); // counting only unregistration events

        resetTaskManageListeners(ClusterHealthAction.NAME);

        // Now run the health on a non-master node - should produce one task on master and one task on another node
        internalCluster().nonMasterClient().admin().cluster().prepareHealth().get();
        assertEquals(2, numberOfEvents(ClusterHealthAction.NAME, Tuple::v1)); // counting only registration events
        assertEquals(2, numberOfEvents(ClusterHealthAction.NAME, event -> event.v1() == false)); // counting only unregistration events
        List<TaskInfo> tasks = findEvents(ClusterHealthAction.NAME, Tuple::v1);

        // Verify that one of these tasks is a parent of another task
        if (tasks.get(0).getParentTaskId().isSet()) {
            assertParentTask(Collections.singletonList(tasks.get(0)), tasks.get(1));
        } else {
            assertParentTask(Collections.singletonList(tasks.get(1)), tasks.get(0));
        }
    }

    public void testTransportReplicationAllShardsTasks() {
        registerTaskManageListeners(FieldStatsAction.NAME);  // main task
        registerTaskManageListeners(FieldStatsAction.NAME + "[s]"); // shard level tasks
        createIndex("test");
        ensureGreen("test"); // Make sure all shards are allocated
        client().prepareFieldStats().setFields("field").get();

        // the field stats operation should produce one main task
        NumShards numberOfShards = getNumShards("test");
        assertEquals(1, numberOfEvents(FieldStatsAction.NAME, Tuple::v1));
        // and then one operation per shard
        assertEquals(numberOfShards.numPrimaries, numberOfEvents(FieldStatsAction.NAME + "[s]", Tuple::v1));

        // the shard level tasks should have the main task as a parent
        assertParentTask(findEvents(FieldStatsAction.NAME + "[s]", Tuple::v1), findEvents(FieldStatsAction.NAME, Tuple::v1).get(0));
    }

    public void testTransportBroadcastByNodeTasks() {
        registerTaskManageListeners(UpgradeAction.NAME);  // main task
        registerTaskManageListeners(UpgradeAction.NAME + "[n]"); // node level tasks
        createIndex("test");
        ensureGreen("test"); // Make sure all shards are allocated
        client().admin().indices().prepareUpgrade("test").get();

        // the percolate operation should produce one main task
        assertEquals(1, numberOfEvents(UpgradeAction.NAME, Tuple::v1));
        // and then one operation per each node where shards are located
        assertEquals(internalCluster().nodesInclude("test").size(), numberOfEvents(UpgradeAction.NAME + "[n]", Tuple::v1));

        // all node level tasks should have the main task as a parent
        assertParentTask(findEvents(UpgradeAction.NAME + "[n]", Tuple::v1), findEvents(UpgradeAction.NAME, Tuple::v1).get(0));
    }

    public void testTransportReplicationSingleShardTasks() {
        registerTaskManageListeners(ValidateQueryAction.NAME);  // main task
        registerTaskManageListeners(ValidateQueryAction.NAME + "[s]"); // shard level tasks
        createIndex("test");
        ensureGreen("test"); // Make sure all shards are allocated
        client().admin().indices().prepareValidateQuery("test").get();

        // the validate operation should produce one main task
        assertEquals(1, numberOfEvents(ValidateQueryAction.NAME, Tuple::v1));
        // and then one operation
        assertEquals(1, numberOfEvents(ValidateQueryAction.NAME + "[s]", Tuple::v1));
        // the shard level operation should have the main task as its parent
        assertParentTask(findEvents(ValidateQueryAction.NAME + "[s]", Tuple::v1), findEvents(ValidateQueryAction.NAME, Tuple::v1).get(0));
    }


    public void testTransportBroadcastReplicationTasks() {
        registerTaskManageListeners(RefreshAction.NAME);  // main task
        registerTaskManageListeners(RefreshAction.NAME + "[s]"); // shard level tasks
        registerTaskManageListeners(RefreshAction.NAME + "[s][*]"); // primary and replica shard tasks
        createIndex("test");
        ensureGreen("test"); // Make sure all shards are allocated
        client().admin().indices().prepareRefresh("test").get();

        // the refresh operation should produce one main task
        NumShards numberOfShards = getNumShards("test");

        logger.debug("number of shards, total: [{}], primaries: [{}] ", numberOfShards.totalNumShards, numberOfShards.numPrimaries);
        logger.debug("main events {}", numberOfEvents(RefreshAction.NAME, Tuple::v1));
        logger.debug("main event node {}", findEvents(RefreshAction.NAME, Tuple::v1).get(0).getTaskId().getNodeId());
        logger.debug("[s] events {}", numberOfEvents(RefreshAction.NAME + "[s]", Tuple::v1));
        logger.debug("[s][*] events {}", numberOfEvents(RefreshAction.NAME + "[s][*]", Tuple::v1));
        logger.debug("nodes with the index {}", internalCluster().nodesInclude("test"));

        assertEquals(1, numberOfEvents(RefreshAction.NAME, Tuple::v1));
        // Because it's broadcast replication action we will have as many [s] level requests
        // as we have primary shards on the coordinating node plus we will have one task per primary outside of the
        // coordinating node due to replication.
        // If all primaries are on the coordinating node, the number of tasks should be equal to the number of primaries
        // If all primaries are not on the coordinating node, the number of tasks should be equal to the number of primaries times 2
        assertThat(numberOfEvents(RefreshAction.NAME + "[s]", Tuple::v1), greaterThanOrEqualTo(numberOfShards.numPrimaries));
        assertThat(numberOfEvents(RefreshAction.NAME + "[s]", Tuple::v1), lessThanOrEqualTo(numberOfShards.numPrimaries * 2));

        // Verify that all [s] events have the proper parent
        // This is complicated because if the shard task runs on the same node it has main task as a parent
        // but if it runs on non-coordinating node it would have another intermediate [s] task on the coordinating node as a parent
        TaskInfo mainTask = findEvents(RefreshAction.NAME, Tuple::v1).get(0);
        List<TaskInfo> sTasks = findEvents(RefreshAction.NAME + "[s]", Tuple::v1);
        for (TaskInfo taskInfo : sTasks) {
            if (mainTask.getTaskId().getNodeId().equals(taskInfo.getTaskId().getNodeId())) {
                // This shard level task runs on the same node as a parent task - it should have the main task as a direct parent
                assertParentTask(Collections.singletonList(taskInfo), mainTask);
            } else {
                String description = taskInfo.getDescription();
                // This shard level task runs on another node - it should have a corresponding shard level task on the node where main task
                // is running
                List<TaskInfo> sTasksOnRequestingNode = findEvents(RefreshAction.NAME + "[s]",
                        event -> event.v1() && mainTask.getTaskId().getNodeId().equals(event.v2().getTaskId().getNodeId())
                                && description.equals(event.v2().getDescription()));
                // There should be only one parent task
                assertEquals(1, sTasksOnRequestingNode.size());
                assertParentTask(Collections.singletonList(taskInfo), sTasksOnRequestingNode.get(0));
            }
        }

        // we will have as many [s][p] and [s][r] tasks as we have primary and replica shards
        assertEquals(numberOfShards.totalNumShards, numberOfEvents(RefreshAction.NAME + "[s][*]", Tuple::v1));

        // we the [s][p] and [s][r] tasks should have a corresponding [s] task on the same node as a parent
        List<TaskInfo> spEvents = findEvents(RefreshAction.NAME + "[s][*]", Tuple::v1);
        for (TaskInfo taskInfo : spEvents) {
            List<TaskInfo> sTask;
            if (taskInfo.getAction().endsWith("[s][p]")) {
                // A [s][p] level task should have a corresponding [s] level task on the same node
                sTask = findEvents(RefreshAction.NAME + "[s]",
                        event -> event.v1() && taskInfo.getTaskId().getNodeId().equals(event.v2().getTaskId().getNodeId())
                                && taskInfo.getDescription().equals(event.v2().getDescription()));
            } else {
                // A [s][r] level task should have a corresponding [s] level task on the a different node (where primary is located)
                sTask = findEvents(RefreshAction.NAME + "[s]",
                    event -> event.v1() && taskInfo.getParentTaskId().getNodeId().equals(event.v2().getTaskId().getNodeId()) && taskInfo
                        .getDescription()
                        .equals(event.v2().getDescription()));
            }
            // There should be only one parent task
            assertEquals(1, sTask.size());
            assertParentTask(Collections.singletonList(taskInfo), sTask.get(0));
        }
    }


    public void testTransportBulkTasks() {
        registerTaskManageListeners(BulkAction.NAME);  // main task
        registerTaskManageListeners(BulkAction.NAME + "[s]");  // shard task
        registerTaskManageListeners(BulkAction.NAME + "[s][p]");  // shard task on primary
        registerTaskManageListeners(BulkAction.NAME + "[s][r]");  // shard task on replica
        createIndex("test");
        ensureGreen("test"); // Make sure all shards are allocated to catch replication tasks
        client().prepareBulk().add(client().prepareIndex("test", "doc", "test_id").setSource("{\"foo\": \"bar\"}")).get();

        // the bulk operation should produce one main task
        assertEquals(1, numberOfEvents(BulkAction.NAME, Tuple::v1));

        // we should also get 1 or 2 [s] operation with main operation as a parent
        // in case the primary is located on the coordinating node we will have 1 operation, otherwise - 2
        List<TaskInfo> shardTasks = findEvents(BulkAction.NAME + "[s]", Tuple::v1);
        assertThat(shardTasks.size(), allOf(lessThanOrEqualTo(2), greaterThanOrEqualTo(1)));

        // Select the effective shard task
        TaskInfo shardTask;
        if (shardTasks.size() == 1) {
            // we have only one task - it's going to be the parent task for all [s][p] and [s][r] tasks
            shardTask = shardTasks.get(0);
            // and it should have the main task as a parent
            assertParentTask(shardTask, findEvents(BulkAction.NAME, Tuple::v1).get(0));
        } else {
            if (shardTasks.get(0).getParentTaskId().equals(shardTasks.get(1).getTaskId())) {
                // task 1 is the parent of task 0, that means that task 0 will control [s][p] and [s][r] tasks
                 shardTask = shardTasks.get(0);
                // in turn the parent of the task 1 should be the main task
                assertParentTask(shardTasks.get(1), findEvents(BulkAction.NAME, Tuple::v1).get(0));
            } else {
                // otherwise task 1 will control [s][p] and [s][r] tasks
                shardTask = shardTasks.get(1);
                // in turn the parent of the task 0 should be the main task
                assertParentTask(shardTasks.get(0), findEvents(BulkAction.NAME, Tuple::v1).get(0));
            }
        }

        // we should also get one [s][p] operation with shard operation as a parent
        assertEquals(1, numberOfEvents(BulkAction.NAME + "[s][p]", Tuple::v1));
        assertParentTask(findEvents(BulkAction.NAME + "[s][p]", Tuple::v1), shardTask);

        // we should get as many [s][r] operations as we have replica shards
        // they all should have the same shard task as a parent
        assertEquals(getNumShards("test").numReplicas, numberOfEvents(BulkAction.NAME + "[s][r]", Tuple::v1));
        assertParentTask(findEvents(BulkAction.NAME + "[s][r]", Tuple::v1), shardTask);
    }

    /**
     * Very basic "is it plugged in" style test that indexes a document and
     * makes sure that you can fetch the status of the process. The goal here is
     * to verify that the large moving parts that make fetching task status work
     * fit together rather than to verify any particular status results from
     * indexing. For that, look at
     * {@link org.elasticsearch.action.support.replication.TransportReplicationActionTests}
     * . We intentionally don't use the task recording mechanism used in other
     * places in this test so we can make sure that the status fetching works
     * properly over the wire.
     */
    public void testCanFetchIndexStatus() throws InterruptedException, ExecutionException, IOException {
        /*
         * We prevent any tasks from unregistering until the test is done so we
         * can fetch them. This will gum up the server if we leave it enabled
         * but we'll be quick so it'll be OK (TM).
         */
        ReentrantLock taskFinishLock = new ReentrantLock();
        taskFinishLock.lock();
        ListenableActionFuture<?> indexFuture = null;
        try {
            CountDownLatch taskRegistered = new CountDownLatch(1);
            for (TransportService transportService : internalCluster().getInstances(TransportService.class)) {
                ((MockTaskManager) transportService.getTaskManager()).addListener(new MockTaskManagerListener() {
                    @Override
                    public void onTaskRegistered(Task task) {
                        if (task.getAction().startsWith(IndexAction.NAME)) {
                            taskRegistered.countDown();
                        }
                    }

                    @Override
                    public void onTaskUnregistered(Task task) {
                        /*
                         * We can't block all tasks here or the task listing task
                         * would never return.
                         */
                        if (false == task.getAction().startsWith(IndexAction.NAME)) {
                            return;
                        }
                        logger.debug("Blocking {} from being unregistered", task);
                        taskFinishLock.lock();
                        taskFinishLock.unlock();
                    }

                    @Override
                    public void waitForTaskCompletion(Task task) {
                    }
                });
            }
            indexFuture = client().prepareIndex("test", "test").setSource("test", "test").execute();
            taskRegistered.await(10, TimeUnit.SECONDS); // waiting for at least one task to be registered

            ListTasksResponse listResponse = client().admin().cluster().prepareListTasks().setActions("indices:data/write/index*")
                    .setDetailed(true).get();
            assertThat(listResponse.getTasks(), not(empty()));
            for (TaskInfo task : listResponse.getTasks()) {
                assertNotNull(task.getStatus());
                GetTaskResponse getResponse = client().admin().cluster().prepareGetTask(task.getTaskId()).get();
                assertFalse("task should still be running", getResponse.getTask().isCompleted());
                TaskInfo fetchedWithGet = getResponse.getTask().getTask();
                assertEquals(task.getId(), fetchedWithGet.getId());
                assertEquals(task.getType(), fetchedWithGet.getType());
                assertEquals(task.getAction(), fetchedWithGet.getAction());
                assertEquals(task.getDescription(), fetchedWithGet.getDescription());
                // The status won't always be equal - it might change between the list and the get.
                assertEquals(task.getStartTime(), fetchedWithGet.getStartTime());
                assertThat(fetchedWithGet.getRunningTimeNanos(), greaterThanOrEqualTo(task.getRunningTimeNanos()));
                assertEquals(task.isCancellable(), fetchedWithGet.isCancellable());
                assertEquals(task.getParentTaskId(), fetchedWithGet.getParentTaskId());
            }
        } finally {
            taskFinishLock.unlock();
            if (indexFuture != null) {
                indexFuture.get();
            }
        }
    }

    public void testTasksCancellation() throws Exception {
        // Start blocking test task
        // Get real client (the plugin is not registered on transport nodes)
        ListenableActionFuture<TestTaskPlugin.NodesResponse> future = TestTaskPlugin.TestTaskAction.INSTANCE.newRequestBuilder(client())
                .execute();
        logger.info("--> started test tasks");

        // Wait for the task to start on all nodes
        assertBusy(() -> assertEquals(internalCluster().size(),
            client().admin().cluster().prepareListTasks().setActions(TestTaskPlugin.TestTaskAction.NAME + "[n]").get().getTasks().size()));

        logger.info("--> cancelling the main test task");
        CancelTasksResponse cancelTasksResponse = client().admin().cluster().prepareCancelTasks()
                .setActions(TestTaskPlugin.TestTaskAction.NAME).get();
        assertEquals(1, cancelTasksResponse.getTasks().size());

        future.get();

        logger.info("--> checking that test tasks are not running");
        assertEquals(0,
                client().admin().cluster().prepareListTasks().setActions(TestTaskPlugin.TestTaskAction.NAME + "*").get().getTasks().size());
    }

    public void testTasksUnblocking() throws Exception {
        // Start blocking test task
        ListenableActionFuture<TestTaskPlugin.NodesResponse> future = TestTaskPlugin.TestTaskAction.INSTANCE.newRequestBuilder(client())
                .execute();
        // Wait for the task to start on all nodes
        assertBusy(() -> assertEquals(internalCluster().size(),
            client().admin().cluster().prepareListTasks().setActions(TestTaskPlugin.TestTaskAction.NAME + "[n]").get().getTasks().size()));

        TestTaskPlugin.UnblockTestTasksAction.INSTANCE.newRequestBuilder(client()).get();

        future.get();
        assertEquals(0, client().admin().cluster().prepareListTasks().setActions(TestTaskPlugin.TestTaskAction.NAME + "[n]").get()
                .getTasks().size());
    }

    public void testListTasksWaitForCompletion() throws Exception {
        waitForCompletionTestCase(randomBoolean(), id -> {
            return client().admin().cluster().prepareListTasks().setActions(TestTaskPlugin.TestTaskAction.NAME)
                    .setWaitForCompletion(true).execute();
        }, response -> {
            assertThat(response.getNodeFailures(), empty());
            assertThat(response.getTaskFailures(), empty());
        });
    }

    public void testGetTaskWaitForCompletionNoPersist() throws Exception {
        waitForCompletionTestCase(false, id -> {
            return client().admin().cluster().prepareGetTask(id).setWaitForCompletion(true).execute();
        }, response -> {
            assertNotNull(response.getTask().getTask());
            assertTrue(response.getTask().isCompleted());
            // We didn't persist the result so it won't come back when we wait
            assertNull(response.getTask().getResponse());
        });
    }

    public void testGetTaskWaitForCompletionWithPersist() throws Exception {
        waitForCompletionTestCase(true, id -> {
            return client().admin().cluster().prepareGetTask(id).setWaitForCompletion(true).execute();
        }, response -> {
            assertNotNull(response.getTask().getTask());
            assertTrue(response.getTask().isCompleted());
            // We persisted the task so we should get its results
            assertEquals(0, response.getTask().getResponseAsMap().get("failure_count"));
        });
    }

    /**
     * Test wait for completion.
     * @param persist should the task persist its results
     * @param wait start waiting for a task. Accepts that id of the task to wait for and returns a future waiting for it.
     * @param validator validate the response and return the task ids that were found
     */
    private <T> void waitForCompletionTestCase(boolean persist, Function<TaskId, ListenableActionFuture<T>> wait, Consumer<T> validator)
            throws Exception {
        // Start blocking test task
        ListenableActionFuture<TestTaskPlugin.NodesResponse> future = TestTaskPlugin.TestTaskAction.INSTANCE.newRequestBuilder(client())
                .setShouldPersistResult(persist).execute();

        ListenableActionFuture<T> waitResponseFuture;
        TaskId taskId;
        try {
            taskId = waitForTestTaskStartOnAllNodes();

            // Wait for the task to start
            assertBusy(() -> client().admin().cluster().prepareGetTask(taskId).get());

            // Register listeners so we can be sure the waiting started
            CountDownLatch waitForWaitingToStart = new CountDownLatch(1);
            for (TransportService transportService : internalCluster().getInstances(TransportService.class)) {
                ((MockTaskManager) transportService.getTaskManager()).addListener(new MockTaskManagerListener() {
                    @Override
                    public void waitForTaskCompletion(Task task) {
                    }

                    @Override
                    public void onTaskRegistered(Task task) {
                    }

                    @Override
                    public void onTaskUnregistered(Task task) {
                        waitForWaitingToStart.countDown();
                    }
                });
            }

            // Spin up a request to wait for the test task to finish
            waitResponseFuture = wait.apply(taskId);

            // Wait for the wait to start
            waitForWaitingToStart.await();
        } finally {
            // Unblock the request so the wait for completion request can finish
            TestTaskPlugin.UnblockTestTasksAction.INSTANCE.newRequestBuilder(client()).get();
        }

        // Now that the task is unblocked the list response will come back
        T waitResponse = waitResponseFuture.get();
        validator.accept(waitResponse);

        future.get();
    }

    public void testListTasksWaitForTimeout() throws Exception {
        waitForTimeoutTestCase(id -> {
            ListTasksResponse response = client().admin().cluster().prepareListTasks()
                    .setActions(TestTaskPlugin.TestTaskAction.NAME).setWaitForCompletion(true).setTimeout(timeValueMillis(100))
                    .get();
            assertThat(response.getNodeFailures(), not(empty()));
            return response.getNodeFailures();
        });
    }

    public void testGetTaskWaitForTimeout() throws Exception {
        waitForTimeoutTestCase(id -> {
            Exception e = expectThrows(Exception.class,
                    () -> client().admin().cluster().prepareGetTask(id).setWaitForCompletion(true).setTimeout(timeValueMillis(100)).get());
            return singleton(e);
        });
    }

    /**
     * Test waiting for a task that times out.
     * @param wait wait for the running task and return all the failures you accumulated waiting for it
     */
    private void waitForTimeoutTestCase(Function<TaskId, ? extends Iterable<? extends Throwable>> wait) throws Exception {
        // Start blocking test task
        ListenableActionFuture<TestTaskPlugin.NodesResponse> future = TestTaskPlugin.TestTaskAction.INSTANCE.newRequestBuilder(client())
                .execute();
        try {
            TaskId taskId = waitForTestTaskStartOnAllNodes();

            // Wait for the task to start
            assertBusy(() -> client().admin().cluster().prepareGetTask(taskId).get());

            // Spin up a request that should wait for those tasks to finish
            // It will timeout because we haven't unblocked the tasks
            Iterable<? extends Throwable> failures = wait.apply(taskId);

            for (Throwable failure : failures) {
                assertNotNull(
                        ExceptionsHelper.unwrap(failure, ElasticsearchTimeoutException.class, ReceiveTimeoutTransportException.class));
            }
        } finally {
            // Now we can unblock those requests
            TestTaskPlugin.UnblockTestTasksAction.INSTANCE.newRequestBuilder(client()).get();
        }
        future.get();
    }

    /**
     * Wait for the test task to be running on all nodes and return the TaskId of the primary task.
     */
    private TaskId waitForTestTaskStartOnAllNodes() throws Exception {
        assertBusy(() -> {
            List<TaskInfo> tasks = client().admin().cluster().prepareListTasks().setActions(TestTaskPlugin.TestTaskAction.NAME + "[n]")
                    .get().getTasks();
            assertEquals(internalCluster().size(), tasks.size());
        });
        List<TaskInfo> task = client().admin().cluster().prepareListTasks().setActions(TestTaskPlugin.TestTaskAction.NAME).get().getTasks();
        assertThat(task, hasSize(1));
        return task.get(0).getTaskId();
    }

    public void testTasksListWaitForNoTask() throws Exception {
        // Spin up a request to wait for no matching tasks
        ListenableActionFuture<ListTasksResponse> waitResponseFuture = client().admin().cluster().prepareListTasks()
                .setActions(TestTaskPlugin.TestTaskAction.NAME + "[n]").setWaitForCompletion(true).setTimeout(timeValueMillis(10))
                .execute();

        // It should finish quickly and without complaint
        assertThat(waitResponseFuture.get().getTasks(), empty());
    }

    public void testTasksGetWaitForNoTask() throws Exception {
        // Spin up a request to wait for no matching tasks
        ListenableActionFuture<GetTaskResponse> waitResponseFuture = client().admin().cluster().prepareGetTask("notfound:1")
                .setWaitForCompletion(true).setTimeout(timeValueMillis(10))
                .execute();

        // It should finish quickly and without complaint
        expectNotFound(() -> waitResponseFuture.get());
    }

    public void testTasksWaitForAllTask() throws Exception {
        // Spin up a request to wait for all tasks in the cluster to make sure it doesn't cause an infinite loop
        ListTasksResponse response = client().admin().cluster().prepareListTasks().setWaitForCompletion(true)
            .setTimeout(timeValueSeconds(10)).get();

        // It should finish quickly and without complaint and list the list tasks themselves
        assertThat(response.getNodeFailures(), emptyCollectionOf(FailedNodeException.class));
        assertThat(response.getTaskFailures(), emptyCollectionOf(TaskOperationFailure.class));
        assertThat(response.getTasks().size(), greaterThanOrEqualTo(1));
    }

    public void testTaskResultPersistence() throws Exception {
        // Randomly create an empty index to make sure the type is created automatically
        if (randomBoolean()) {
            logger.info("creating an empty results index with custom settings");
            assertAcked(client().admin().indices().prepareCreate(TaskPersistenceService.TASK_INDEX));
        }

        registerTaskManageListeners(TestTaskPlugin.TestTaskAction.NAME);  // we need this to get task id of the process

        // Start non-blocking test task
        TestTaskPlugin.TestTaskAction.INSTANCE.newRequestBuilder(client()).setShouldPersistResult(true).setShouldBlock(false).get();

        List<TaskInfo> events = findEvents(TestTaskPlugin.TestTaskAction.NAME, Tuple::v1);

        assertEquals(1, events.size());
        TaskInfo taskInfo = events.get(0);
        TaskId taskId = taskInfo.getTaskId();

        GetResponse resultDoc = client()
                .prepareGet(TaskPersistenceService.TASK_INDEX, TaskPersistenceService.TASK_TYPE, taskId.toString()).get();
        assertTrue(resultDoc.isExists());

        Map<String, Object> source = resultDoc.getSource();
        @SuppressWarnings("unchecked")
        Map<String, Object> task = (Map<String, Object>) source.get("task");
        assertEquals(taskInfo.getTaskId().getNodeId(), task.get("node"));
        assertEquals(taskInfo.getAction(), task.get("action"));
        assertEquals(Long.toString(taskInfo.getId()), task.get("id").toString());

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) source.get("response");
        assertEquals("0", result.get("failure_count").toString());

        assertNull(source.get("failure"));

        assertNoFailures(client().admin().indices().prepareRefresh(TaskPersistenceService.TASK_INDEX).get());

        SearchResponse searchResponse = client().prepareSearch(TaskPersistenceService.TASK_INDEX)
            .setTypes(TaskPersistenceService.TASK_TYPE)
            .setSource(SearchSourceBuilder.searchSource().query(QueryBuilders.termQuery("task.action", taskInfo.getAction())))
            .get();

        assertEquals(1L, searchResponse.getHits().totalHits());

        searchResponse = client().prepareSearch(TaskPersistenceService.TASK_INDEX).setTypes(TaskPersistenceService.TASK_TYPE)
                .setSource(SearchSourceBuilder.searchSource().query(QueryBuilders.termQuery("task.node", taskInfo.getTaskId().getNodeId())))
                .get();

        assertEquals(1L, searchResponse.getHits().totalHits());

        GetTaskResponse getResponse = expectFinishedTask(taskId);
        assertEquals(result, getResponse.getTask().getResponseAsMap());
        assertNull(getResponse.getTask().getError());
    }

    public void testTaskFailurePersistence() throws Exception {
        registerTaskManageListeners(TestTaskPlugin.TestTaskAction.NAME);  // we need this to get task id of the process

        // Start non-blocking test task that should fail
        assertThrows(
            TestTaskPlugin.TestTaskAction.INSTANCE.newRequestBuilder(client())
                .setShouldFail(true)
                .setShouldPersistResult(true)
                .setShouldBlock(false),
            IllegalStateException.class
        );

        List<TaskInfo> events = findEvents(TestTaskPlugin.TestTaskAction.NAME, Tuple::v1);
        assertEquals(1, events.size());
        TaskInfo failedTaskInfo = events.get(0);
        TaskId failedTaskId = failedTaskInfo.getTaskId();

        GetResponse failedResultDoc = client()
            .prepareGet(TaskPersistenceService.TASK_INDEX, TaskPersistenceService.TASK_TYPE, failedTaskId.toString())
            .get();
        assertTrue(failedResultDoc.isExists());

        Map<String, Object> source = failedResultDoc.getSource();
        @SuppressWarnings("unchecked")
        Map<String, Object> task = (Map<String, Object>) source.get("task");
        assertEquals(failedTaskInfo.getTaskId().getNodeId(), task.get("node"));
        assertEquals(failedTaskInfo.getAction(), task.get("action"));
        assertEquals(Long.toString(failedTaskInfo.getId()), task.get("id").toString());

        @SuppressWarnings("unchecked")
        Map<String, Object> error = (Map<String, Object>) source.get("error");
        assertEquals("Simulating operation failure", error.get("reason"));
        assertEquals("illegal_state_exception", error.get("type"));

        assertNull(source.get("result"));

        GetTaskResponse getResponse = expectFinishedTask(failedTaskId);
        assertNull(getResponse.getTask().getResponse());
        assertEquals(error, getResponse.getTask().getErrorAsMap());
    }

    public void testGetTaskNotFound() throws Exception {
        // Node isn't found, tasks index doesn't even exist
        expectNotFound(() -> client().admin().cluster().prepareGetTask("not_a_node:1").get());

        // Node exists but the task still isn't found
        expectNotFound(() -> client().admin().cluster().prepareGetTask(new TaskId(internalCluster().getNodeNames()[0], 1)).get());
    }

    public void testNodeNotFoundButTaskFound() throws Exception {
        // Save a fake task that looks like it is from a node that isn't part of the cluster
        CyclicBarrier b = new CyclicBarrier(2);
        TaskPersistenceService resultsService = internalCluster().getInstance(TaskPersistenceService.class);
        resultsService.persist(
                new PersistedTaskInfo(new TaskInfo(new TaskId("fake", 1), "test", "test", "", null, 0, 0, false, TaskId.EMPTY_TASK_ID),
                        new RuntimeException("test")),
                new ActionListener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        try {
                            b.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        b.await();

        // Now we can find it!
        GetTaskResponse response = expectFinishedTask(new TaskId("fake:1"));
        assertEquals("test", response.getTask().getTask().getAction());
        assertNotNull(response.getTask().getError());
        assertNull(response.getTask().getResponse());
    }

    @Override
    public void tearDown() throws Exception {
        for (Map.Entry<Tuple<String, String>, RecordingTaskManagerListener> entry : listeners.entrySet()) {
            ((MockTaskManager) internalCluster().getInstance(TransportService.class, entry.getKey().v1()).getTaskManager())
                    .removeListener(entry.getValue());
        }
        listeners.clear();
        super.tearDown();
    }

    /**
     * Registers recording task event listeners with the given action mask on all nodes
     */
    private void registerTaskManageListeners(String actionMasks) {
        for (String nodeName : internalCluster().getNodeNames()) {
            DiscoveryNode node = internalCluster().getInstance(ClusterService.class, nodeName).localNode();
            RecordingTaskManagerListener listener = new RecordingTaskManagerListener(node, actionMasks.split(","));
            ((MockTaskManager) internalCluster().getInstance(TransportService.class, nodeName).getTaskManager()).addListener(listener);
            RecordingTaskManagerListener oldListener = listeners.put(new Tuple<>(node.getName(), actionMasks), listener);
            assertNull(oldListener);
        }
    }

    /**
     * Resets all recording task event listeners with the given action mask on all nodes
     */
    private void resetTaskManageListeners(String actionMasks) {
        for (Map.Entry<Tuple<String, String>, RecordingTaskManagerListener> entry : listeners.entrySet()) {
            if (actionMasks == null || entry.getKey().v2().equals(actionMasks)) {
                entry.getValue().reset();
            }
        }
    }

    /**
     * Returns the number of events that satisfy the criteria across all nodes
     *
     * @param actionMasks action masks to match
     * @return number of events that satisfy the criteria
     */
    private int numberOfEvents(String actionMasks, Function<Tuple<Boolean, TaskInfo>, Boolean> criteria) {
        return findEvents(actionMasks, criteria).size();
    }

    /**
     * Returns all events that satisfy the criteria across all nodes
     *
     * @param actionMasks action masks to match
     * @return number of events that satisfy the criteria
     */
    private List<TaskInfo> findEvents(String actionMasks, Function<Tuple<Boolean, TaskInfo>, Boolean> criteria) {
        List<TaskInfo> events = new ArrayList<>();
        for (Map.Entry<Tuple<String, String>, RecordingTaskManagerListener> entry : listeners.entrySet()) {
            if (actionMasks == null || entry.getKey().v2().equals(actionMasks)) {
                for (Tuple<Boolean, TaskInfo> taskEvent : entry.getValue().getEvents()) {
                    if (criteria.apply(taskEvent)) {
                        events.add(taskEvent.v2());
                    }
                }
            }
        }
        return events;
    }

    /**
     * Asserts that all tasks in the tasks list have the same parentTask
     */
    private void assertParentTask(List<TaskInfo> tasks, TaskInfo parentTask) {
        for (TaskInfo task : tasks) {
            assertParentTask(task, parentTask);
        }
    }

    private void assertParentTask(TaskInfo task, TaskInfo parentTask) {
        assertTrue(task.getParentTaskId().isSet());
        assertEquals(parentTask.getTaskId().getNodeId(), task.getParentTaskId().getNodeId());
        assertTrue(Strings.hasLength(task.getParentTaskId().getNodeId()));
        assertEquals(parentTask.getId(), task.getParentTaskId().getId());
    }

    private ResourceNotFoundException expectNotFound(ThrowingRunnable r) {
        Exception e = expectThrows(Exception.class, r);
        ResourceNotFoundException notFound = (ResourceNotFoundException) ExceptionsHelper.unwrap(e, ResourceNotFoundException.class);
        if (notFound == null) throw new RuntimeException("Expected ResourceNotFoundException", e);
        return notFound;
    }

    /**
     * Fetch the task status from the list tasks API using it's "fallback to get from the task index" behavior. Asserts some obvious stuff
     * about the fetched task and returns a map of it's status.
     */
    private GetTaskResponse expectFinishedTask(TaskId taskId) throws IOException {
        GetTaskResponse response = client().admin().cluster().prepareGetTask(taskId).get();
        assertTrue("the task should have been completed before fetching", response.getTask().isCompleted());
        TaskInfo info = response.getTask().getTask();
        assertEquals(taskId, info.getTaskId());
        assertNull(info.getStatus()); // The test task doesn't have any status
        return response;
    }
}
