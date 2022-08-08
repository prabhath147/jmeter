#!/usr/bin/env groovy
node {

    stage('Initialise') {
        /* Checkout the scripts */
        checkout scm: [
                $class: 'GitSCM',
                userRemoteConfigs: [
                        [
                                url: "https://github.com/prabhath147/jmeter.git",
                                credentialsId: "dc6211ae-b8a3-4519-9940-f85375ee3fe3"
                        ]
                ],
                branches: [[name: "main"]]
        ], poll: false
    }

	stage('Jmeter'){
		steps{
			echo 'Jmeter'
		}
	}
	stage('Execute Performance Tests') {
        dir("${WORKSPACE}/test plan") {
			bat 'c:/apache-jmeter/apache-jmeter/bin/jmeter.bat -n -t Shift-Left.jmx -l Shift-Left.jtl'
        }
      }
	stage('Jmeter test file'){
		steps{
			dir("${WORKSPACE}/test plan") {
			bat 'del Shift-Left.jtl'
			bat 'C:\\Training\\Jmeter\\apache-jmeter-5.5\\bin\\jmeter.bat -n -t Test_Plan3.jmx -l Shift-Left.jtl'
			 }
		}
	}
	stage('test response time result'){
		steps{
			script{
			perfReport errorFailedThreshold: 1, errorUnstableResponseTimeThreshold: 'Shift-Left.jtl:20', filterRegex: '', modePerformancePerTestCase: true, showTrendGraphs: true, sourceDataFiles: 'Shift-Left.jtl'
			echo "${currentBuild.result}"
			}
		}
	}
	stage('Jmeter generate csv'){
		steps{
			script{
			
			bat 'del test1.csv'
			bat 'C:\\Training\\Jmeter\\apache-jmeter-5.5\\bin\\JMeterPluginsCMD.bat --generate-csv test1.csv --input-jtl Shift-Left.jtl --plugin-type SynthesisReport'
			
			}
		}
	}
	stage('test throughput value'){
		steps {
			script{
			
				def records = readFile file: 'test1.csv'
				def lines=records.readLines()
			   
				def lastline=lines.get(lines.size()-1).split(",")    
				  
				echo "${lastline[8]}"
				float f = Float.valueOf(lastline[8]); 
				echo "${f}"
				if(f<3){
				  currentBuild.result='Failure'
				}
			}
		
		}
	}
}
