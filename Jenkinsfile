pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/home/ec2-user/api-gateway"
        EC2_HOST = "13.53.39.170"
        SERVICE_NAME = "api-gateway"
        PEM_PATH = "C:\\Users\\KRISHNA\\.ssh\\ec2-key.pem"
    }

    tools {
        jdk 'Java17'
        maven 'Maven3'
        git 'Git'
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
        sshagent(['ec2-key']) {
            bat """
            echo ===== Copying JAR to EC2 =====
            scp -o StrictHostKeyChecking=no target\\${SERVICE_NAME}.jar ec2-user@${EC2_HOST}:${DEPLOY_DIR}/

            echo ===== Stopping old instance =====
            ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "pkill -f ${SERVICE_NAME}.jar || true"

            echo ===== Starting new instance =====
            ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar > ${DEPLOY_DIR}/${SERVICE_NAME}.log 2>&1 &"
            """
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
