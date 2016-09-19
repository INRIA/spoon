var log      = require('git-log-parser');
var through2 = require('through2');
var request = require('request');

var SPOON_REPOSITORY = "https://github.com/INRIA/spoon/";
log.fields.merge = "P";

var prefix = {
"feat": {title:"New feature", commits: []},
"fix": {title:"Fixes", commits: []},
"doc": {title:"Documentation", commits: []},
"style": {title:"Code style", commits: []},
"refactor": {title:"Refactoring", commits: []},
"perf": {title:"Performance", commits: []},
"test": {title:"Tests", commits: []},
"chore": {title:"Other", commits: []},
"unknown": {title:"Unknown", commits: []}
};

var count = 0;
var prs = {};

if (process.argv.length == 2) {
	console.error("Invalid script usage! node changelog.js <previous-spoon-version>");
	return;
}
var SPOON_VERSION = process.argv[2];

log.parse({
	"_": "spoon-core-" + SPOON_VERSION + "..master"
})
.pipe(through2.obj(function (chunk, enc, callback) {
	chunk.subject = chunk.subject.replace("[ci skip]", "").trim();
	count ++;
	var found = false;
	if(chunk.merge) {
		var mergeSplit = chunk.merge.split(" ");
		
		if(mergeSplit.length == 2) {
			chunk.prId = mergeSplit[1];
			chunk.origin = mergeSplit[0];
		} else {
			chunk.prId = mergeSplit[0];
		}

		prs[chunk.prId] = chunk;
	}
	var url = SPOON_REPOSITORY + "branch_commits/" + chunk.commit.long;
	request(url, function (error, response, body) {
		var mMerge = chunk.subject.match("Merge pull request (#[0-9]+)");
		if(mMerge) {
			chunk.pr = mMerge[1];
			found = true;
		} else if(prs[chunk.commit.long]) {
			chunk.pr = prs[chunk.commit.long].pr;
			found = true;
		} 
		if (!error && response.statusCode == 200) {
			var match = body.match(/">#([0-9]+)<\/a>/);
			if (match) {
				chunk.pr = "#" + match[1];
				found = true;
			}
		}

		var foundPrefix = false;
		for(var pref in prefix) {
			var index = chunk.subject.toLowerCase().indexOf(pref);
			if(index > -1 && index < 2) {
				prefix[pref].commits.push(chunk);
				foundPrefix = true;
				var m = chunk.subject.substring(0, chunk.subject.indexOf(":") - 1)
				if(m) {
					if(m.indexOf("(") > 0) {
						m = m.substring(m.indexOf("(") + 1)
						chunk.category = m;
					}
				}
				break;
			}
		}
		if (!foundPrefix && chunk.subject.indexOf("Merge pull request") == -1) {
			prefix["unknown"].commits.push(chunk);
		}
		var regex = /(#[0-9]+)/;
		var m = chunk.subject.match(regex);
		if(m) {
			if (m[0] != chunk.pr) {
				chunk.issue = m[0]
			}
		} else {
			m = chunk.body.match(regex);
			if(m) {
				if (m[0] != chunk.pr) {
					chunk.issue = m[0]
				}
			}
		}
		callback(null, JSON.stringify(prefix, undefined, 2));
	});
})).on("data", function () {
}).on("end", function () {
	// print the changelog
	for(var i in prefix) {
		var c = prefix[i];
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
				output += "* **" + category + "**";
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
					output += " : ";
				} else {
					output += "* ";
				}
				output += printCommit(categories[category][0]);
			}
			console.log(output.trim());
		}
	}
});

function printCommit(commit) {
	var issue = "";
	if(commit.issue) {
		issue = " (Issue: " + commit.issue + ")";
	}
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
	return subject + issue + pr;
}
