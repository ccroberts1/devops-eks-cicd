def incrementVersion() {
    echo 'incrementing app version...'
    sh 'mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit'
    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
    def version = matcher[0][1]
    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
}
def buildJar() {
    echo 'building the application...'
    sh 'mvn clean package'
}

def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh 'docker build -t ccroberts1/demo-app:${IMAGE_NAME} .'
        sh 'echo $PASS | docker login -u $USER --password-stdin'
        sh 'docker push ccroberts1/demo-app:${IMAGE_NAME}'
    }
}

def deployApp() {
    echo 'deploying the application...'
    sh 'envsubst < kubernetes/deployment.yaml | kubectl apply -f '
    sh 'envsubst < kubernetes/service.yaml | kubectl apply -f '
}

def commitVersionUpdate() {
    withCredentials([string(credentialsId: 'github-token', variable: 'TOKEN')]) {
        sh 'git config --global user.email "jenkins@example.com"'
        sh 'git config --global user.name "jenkins"'

        sh 'git status'
        sh 'git branch'

        sh 'git add .'
        sh 'git commit -m "ci: version bump"'
        sh "git push https://${TOKEN}@github.com:ccroberts1/devops-eks-cicd.git HEAD:master"
    }
}

return this
