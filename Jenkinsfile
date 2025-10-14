pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/home/ec2-user"
        EC2_HOST = "13.60.47.188"
        SERVICE_NAME = "api-gateway"
        PEM_PATH = "C:\\ProgramData\\Jenkins\\.ssh\\krishna.pem"
        SERVER_PORT = "8085"
        LOG_FILE = "api-gateway.log"
    }

    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    stages {

        stage('Checkout') {
            steps {
                git url: 'https://github.com/Pransquare/Api-Gateway.git', branch: 'master'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy to EC2') {
    steps {
        script {
            def remote = [:]
            remote.name = 'ec2-server'
            remote.host = "${EC2_HOST}"
            remote.user = 'ec2-user'
            remote.identityFile = "${PEM_PATH}"
            remote.allowAnyHosts = true

            echo "===== Copying JAR to EC2 ====="
            sshPut remote: remote, from: "target/${SERVICE_NAME}.jar", into: "${DEPLOY_DIR}/"

            echo "===== Stopping old API Gateway instance if running ====="
            sshCommand remote: remote, command: '''
                if pgrep -f api-gateway.jar > /dev/null; then
                    pkill -f api-gateway.jar
                else
                    echo "No running instance of api-gateway.jar found."
                fi
            ''', ignoreExitStatus: true

            echo "===== Starting new API Gateway instance ====="
            sshCommand remote: remote, command: """
                nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar \
                --server.port=${SERVER_PORT} > ${DEPLOY_DIR}/${LOG_FILE} 2>&1 &
            """

            echo "✅ Deployment completed successfully!"
        }
    }
}

    }

    post {
        failure {
            echo "❌ Deployment failed. Check Jenkins console logs for details."
        }
    }
}
