pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'desa',
                    url: 'https://github.com/liopalacios/sb-security-base',
                    credentialsId: 'github-lio'
            }
        }
        stage('Prueba') {
            steps {
                echo "✅ Jenkinsfile detectado y ejecutándose en branch desa"
            }
        }
    }
}
