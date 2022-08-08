pipeline {
    agent any
	environment {
		OUTPUT_PATH="C:\\Training\\Jmeter\\jmeter\\test_plan\\"
		
	}
	stages{
    stage('Initialise') {
	steps{
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
	}

	stage('Jmeter'){
		
			steps{
                echo 'Jmeter'
            }
		
	}
	
	stage('Jmeter test file'){
		steps{
			dir("${WORKSPACE}\\test_plan") {
			//bat 'del C:\\Training\\Jmeter\\jmeter\\test_plan\\Shift-Left.jtl'
			bat 'C:\\Training\\Jmeter\\apache-jmeter-5.5\\bin\\jmeter.bat -n -t Test_Plan3.jmx -l ${OUTPUT_PATH}Shift-Left.jtl'
			 }
		}
	}
	stage('test response time result'){
		steps{
			script{
			perfReport errorFailedThreshold: 10, errorUnstableResponseTimeThreshold: 'Shift-Left.jtl:20', filterRegex: '', modePerformancePerTestCase: true, showTrendGraphs: true, sourceDataFiles: ${OUTPUT_PATH}Shift-Left.jtl'
			echo "${currentBuild.result}"
			}
		}
	}
	stage('Jmeter generate csv'){
		steps{
			script{			
			//bat 'del C:\\Training\\Jmeter\\jmeter\\test_plan\\test1.csv'
			bat 'C:\\Training\\Jmeter\\apache-jmeter-5.5\\bin\\JMeterPluginsCMD.bat --generate-csv ${OUTPUT_PATH}test1.csv --input-jtl ${OUTPUT_PATH}Shift-Left.jtl --plugin-type SynthesisReport'
			
			}
		}
	}
	stage('test throughput value'){
		steps{
			script{
			
				def records = readFile file: ${OUTPUT_PATH}'test1.csv'
				def lines=records.readLines()
			   
				def lastline=lines.get(lines.size()-1).split(",")    
				  
				echo "${lastline[8]}"
				float throughput = Float.valueOf(lastline[8]); 
				echo "${throughput}"
				if(throughput<3){
				  currentBuild.result='Failure'
				}
			}
		}	
	}
	stage('delete Jmeter files'){
		steps{
			dir("${WORKSPACE}\\test_plan") {
			
			bat 'del ${OUTPUT_PATH}Shift-Left.jtl'
			bat 'del ${OUTPUT_PATH}test1.csv'
			 }
		}
	}
	}
}
