package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'CommitStats'
in the root project, and delete the patch script.
*/
create(DslContext.projectId, BuildType({
    id("CommitStats")
    name = "commit-stats"
    description = "Publish commit stats"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        exec {
            name = "publish"
            enabled = false
            path = "%teamcity.tool.git_commit_stats%/git_commit_stats.py"
            arguments = "--format influx --all"
        }
        script {
            name = "debug"
            enabled = false
            scriptContent = """
                ls -la %teamcity.agent.tools.dir%/git_commit_stats/
                ls -la `command -v %teamcity.tool.git_commit_stats%`
            """.trimIndent()
        }
        script {
            name = "publish (1)"
            scriptContent = "%teamcity.tool.singlife-cli%/git_commit_stats.py --format influx | %teamcity.tool.singlife-cli%/post -url http://influxdb:8086/write?db=git_commit_stats&precision=s"
        }
    }

    triggers {
        schedule {
            schedulingPolicy = daily {
                minute = 30
                timezone = "Asia/Singapore"
            }
            branchFilter = ""
            triggerBuild = always()
            withPendingChangesOnly = false
        }
    }
}))

