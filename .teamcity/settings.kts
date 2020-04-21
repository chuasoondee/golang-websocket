import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2019.2"

project {
    description = "Simple web application on websocket with golang and vue"

    buildType(Test)
    buildType(Build)
    buildType(Release)
    buildTypesOrder = arrayListOf(Test, Build, Release)
}

object Release : BuildType({
    name = "release"
    description = "Release application binaries"

    steps {
        script {
            name = "release"
            scriptContent = "echo 'release step'"
        }
    }

    dependencies {
        snapshot(Build) {
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
    }
})


object Build : BuildType({
    name = "build"
    description = "Build application binaries"

    steps {
        script {
            name = "build"
            scriptContent = "echo 'build step'"
        }
    }

    dependencies {
        snapshot(Test) {
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
    }
})

object Test : BuildType({
    name = "test"
    description = "Run test suite"

    steps {
        script {
            name = "test"
            scriptContent = "echo 'test step'"
        }
    }
})
