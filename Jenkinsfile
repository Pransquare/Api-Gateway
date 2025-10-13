pipeline {
    agent any

    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    environment {
        EC2_HOST = "13.53.193.215"           // ✅ Replace with your EC2 public IP
        EC2_USER = "Administrator"          // ✅ Windows EC2 username
        EC2_PASS = "d8%55Ir.%Z!hNR%VgUe-07OYX0ujLy;S"     // ✅ Administrator password
        DEPLOY_DIR = "C:\\Apps\\api-gateway"
        SERVICE_PORT = "8765"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "🔹 Checking out API Gateway repository..."
                git branch: 'master', url: 'https://github.com/Pransquare/Api-Gateway.git'
            }
        }

        stage('Build') {
            steps {
                echo "🏗️ Building API Gateway JAR..."
                bat '''
                    mvn clean package -DskipTests
                    if exist target\\api-gateway-0.0.1-SNAPSHOT.jar (
                        rename target\\api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar
                    )
                '''
            }
        }

       stage('Deploy to EC2') {
    steps {
        script {
            echo "🚀 Deploying API Gateway to EC2 (${EC2_HOST})..."

            // ✅ Copy JAR to EC2
            bat """
                echo 📦 Copying JAR to EC2...
                net use \\\\\\\${EC2_HOST}\\C\$ /user:${EC2_USER} ${EC2_PASS}
                if not exist \\\\\\\${EC2_HOST}\\C\$\\Apps\\api-gateway mkdir \\\\\\\${EC2_HOST}\\C\$\\Apps\\api-gateway
                copy target\\api-gateway.jar \\\\\\\${EC2_HOST}\\C\$\\Apps\\api-gateway\\ /Y
                net use \\\\\\\${EC2_HOST}\\C\$ /delete
            """

            // ✅ Stop old Java process and start new one remotely
            bat """
                echo 🔁 Restarting API Gateway on EC2...
                "C:\\Tools\\PsExec.exe" \\\\\\\${EC2_HOST} -u ${EC2_USER} -p ${EC2_PASS} -h -d cmd /c ^
                "taskkill /F /IM java.exe & cd ${DEPLOY_DIR} & start java -jar api-gateway.jar --server.port=${SERVICE_PORT}"
            """
        }
    }
}
    }

    post {
        always {
            echo "✅ API Gateway deployment finished."
        }
        failure {
            echo "❌ Pipeline failed during deployment!"
        }
    }
}
