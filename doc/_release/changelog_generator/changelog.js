var log      = require('git-log-parser');
var through2 = require('through2');
var https = require('https');

// hide merge commits
log.fields.merge = "P";

var SPOON_REPOSITORY = "https://github.com/INRIA/spoon/";

/**
 * Return the usage of the script
 * @returns {string}
 */
function usage() {
	return "Invalid script usage! node changelog.js <previous-spoon-version>";
}

/**
 * Main function of the script
 */
function main() {
    if (process.argv.length == 2) {
        console.error(usage());
        return;
    }
    parseCommitLog(process.argv[2], function (data) {});
}

/**
 * Parse the log of spoon
 * @param the last version of spoon (ex: 5.5.0)
 * @param callback(data): the callback function with one argument that contains the categorized commits
 */
function parseCommitLog(version, callback) {
    var categorizedCommits = {
        "feat": { // will also match "feature:"
            title:"New features",
            commits: []
        },
        "fix": {
            title:"Bug fixes",
            commits: []
        },
        "doc": {
            title:"Documentation",
            commits: []
        },
        "style": {
            title:"Code style",
            commits: []
        },
        "ref": { // will also match "refactor:"
            title:"Refactoring",
            commits: []
        },
        "perf": {
            title:"Performance",
            commits: []
        },
        "test": {
            title:"Tests",
            commits: []
        },
        "chore": {
            title:"Other",
            commits: []
        },
        "unknown": {
            title:"Unknown",
            commits: []
        }
    };
    log
        .parse({
            "_": "spoon-core-" + version + "..master"
        })
        .pipe(through2.obj(function (commit, enc, callback) {
            commit.subject = commit.subject.replace("[ci skip]", "").trim();

            var found = false;

            var mMerge = commit.subject.match("Merge pull request (#[0-9]+)");
            if(mMerge) {
                commit.pr = mMerge[1];
                found = true;
            }

			var category = getCommitCategory(commit, categorizedCommits);
            if (category != null) {
                categorizedCommits[category].commits.push(commit);
                process.stdout.write("\033[2K" + categorizedCommits[category]['title'] + ": " + printCommit(commit) + '\033[0G');
			}

            if (!found) {
                var url = SPOON_REPOSITORY + "branch_commits/" + commit.commit.long;
                https.get(url, function (response) {
                    let body = '';
                    response.on('data', function (chunk) {
                        body += chunk;
                    });
                    response.on('end', function () {
                        var match = body.match(/">#([0-9]+)<\/a>/);
                        if (match) {
                            commit.pr = "#" + match[1];
                            found = true;
                        }
                        callback(null, null);
                    });
                }).on('error', function (error) {
                    console.error(error);
                    callback(null, null);
                });
            } else {
                callback(null, null);
			}
        }))
        .on("data", function () {})
        .on("end", function () {
        	// clear progression
        	console.log("\033[2K");

            var authors = {};

            // print the changelog
            for(var i in categorizedCommits) {
                var c = categorizedCommits[i];
                if (c.commits.length == 0) {
                    continue;
                }
                console.log("\n");
                console.log("# " + c.title + "\n");
                var commits = c.commits.sort(function (a, b) {
                    if(a.category == b.category) {
                        return 0;
                    }
                    if(a.category > b.category) {
                        return 1;
                    }
                    return -1
                });
                var categories = {};
                for (var j = 0; j < commits.length; j++) {
                    var commit = commits[j];
                    if (authors[commit.author.name] == null) {
                        authors[commit.author.name] = {
                            "pr": 0
                        };
                    }
                    authors[commit.author.name]['pr'] ++;
                    var category = commit.category;
                    if (!category) {
                        category = "none";
                    }
                    if (!categories[category]) {
                        categories[category] = []
                    }
                    categories[category].push(commit);
                }
                for (category in categories) {
                    var output = "";
                    if (category != "none") {
                        output += "* " + category + "";
                    }
                    if (categories[category].length > 1) {
                        output += "\n";
                        for (k in categories[category]) {
                            var commit = categories[category][k];
                            if (category != "none") {
                                output += "  ";
                            }
                            output += "* " + printCommit(commit) + "\n";
                        }
                    } else {
                        if (category != "none") {
                            output += ": ";
                        } else {
                            output += "* ";
                        }
                        output += printCommit(categories[category][0]);
                    }
                    console.log(output.trim());
                }
            }

            // Print authors of the release
            console.log("\n# Authors");
            console.log("| Name    | Nb Commit |");
            console.log("|---------|-----------|");
            var names = [];
            for (var author in authors) {
                names[names.length] = author;
            }
            names = names.sort(function (a, b) {
                return authors[b]['pr'] - authors[a]['pr'];
            });
            for (var index in names) {
                var author = names[index];
                console.log("| " + author + " | " + authors[author]['pr'] + " |");
            }
            if (callback) {
                callback(categorizedCommits);
            }
        });
}

/**
 * Get the category if the commit
 * @param commit
 */
function getCommitCategory (commit, categorizedCommits) {
    for(var pref in categorizedCommits) {
        var index = commit.subject.toLowerCase().indexOf(pref);
        if(index > -1 && index < 2) {
            var m = commit.subject.substring(0, commit.subject.indexOf(":") - 1);
            if(m) {
                if(m.indexOf("(") > 0) {
                    m = m.substring(m.indexOf("(") + 1)
                    commit.category = m;
                }
            }
            return pref;
        }
    }
    // ignore merge commit
    if (commit.subject.indexOf("Merge pull request") > -1) {
        return null;
    }
    if (commit.subject.indexOf("Merge remote-tracking branch") > -1) {
        return null;
    }
    return "unknown";
}

function printCommit(commit) {
	var pr = "";
	if(commit.pr) {
		pr = " (PR: " + commit.pr + ")";
	}
	var subject = commit.subject.substring(commit.subject.indexOf(':') + 1).replace(/^\[[^\]]+\]/, '');
	subject = subject.replace(/\([a-zA-Z ]*#[0-9]+\)/, '').trim();
	if (subject[subject.length - 1] != '.') {
		subject += ".";
	}
	var firstCharacter = subject[0];
	if (firstCharacter == firstCharacter.toLowerCase()) {
	 	subject = firstCharacter.toUpperCase() + subject.substring(1);
	}
	return subject + pr;
}

main();
