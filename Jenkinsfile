pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/home/ec2-user/api-gateway"
        EC2_HOST = "13.51.195.68"
        SERVICE_NAME = "api-gateway"
        PEM_PATH = "C:\\ProgramData\\Jenkins\\.ssh\\krishna.pem"
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

        stage('Build JAR') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Prepare EC2 Directory') {
            steps {
                bat """
                echo ===== Create remote directory if not exists =====
                ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "mkdir -p ${DEPLOY_DIR}"
                """
            }
        }

        stage('Copy JAR to EC2') {
            steps {
                bat """
                echo ===== Copying JAR to EC2 =====
                scp -i "${PEM_PATH}" -o StrictHostKeyChecking=no target\\${SERVICE_NAME}.jar ec2-user@${EC2_HOST}:${DEPLOY_DIR}/
                """
            }
        }

        stage('Deploy App') {
            steps {
                bat """
                echo ===== Stopping old app if running =====
                ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "pkill -f ${SERVICE_NAME}.jar || true"

                echo ===== Starting new app =====
                ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar > ${DEPLOY_DIR}/${SERVICE_NAME}.log 2>&1 &"

                echo ✅ Deployment completed successfully!
                """
            }
        }
    }

    post {
        failure {
            echo "❌ Deployment failed. Check Jenkins console logs for details."
        }
    }
}
