pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/home/ec2-user"
        EC2_HOST = "13.60.47.188"
        SERVICE_NAME = "api-gateway"
        SERVER_PORT = "8085"
        LOG_FILE = "api-gateway.log"
        SSH_CREDENTIALS_ID = "ec2-linux-key"  
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
                    remote.name = "ec2-server"
                    remote.host = "${EC2_HOST}"
                    remote.allowAnyHosts = true
                    remote.user = "ec2-user" // or ubuntu
                    remote.identityFile = "C:\\ProgramData\\Jenkins\\.ssh\\krishna.pem" // Optional if not using credentials
                    remote.credentialsId = "${SSH_CREDENTIALS_ID}"

              
                    echo "===== Copying JAR to EC2 ====="
                    sshPut remote: remote, from: "target/${SERVICE_NAME}.jar", into: "${DEPLOY_DIR}/"

                  
                    echo "===== Stopping old instance if running ====="
                    sshCommand remote: remote, command: "pkill -f ${SERVICE_NAME}.jar || true"

                   
                    echo "===== Starting new instance ====="
                    sshCommand remote: remote, command: """
                        nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar \
                        --server.port=${SERVER_PORT} > ${DEPLOY_DIR}/${LOG_FILE} 2>&1 &
                    """

              
                    echo "===== Checking process ====="
                    sshCommand remote: remote, command: "ps -ef | grep ${SERVICE_NAME}.jar"

                    
                    echo "===== Recent Logs ====="
                    sshCommand remote: remote, command: "tail -n 10 ${DEPLOY_DIR}/${LOG_FILE}"
                }
            }
        }
    }

    post {
        failure {
            echo " Deployment failed. Check Jenkins console logs for details."
        }
        success {
            echo " Deployment completed successfully!"
        }
    }
}
