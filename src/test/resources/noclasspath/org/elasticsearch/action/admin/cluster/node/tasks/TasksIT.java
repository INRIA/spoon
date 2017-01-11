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
    }
}
