import { WorkerService } from './WorkerService';
const stompit = require('stompit');

/**
 * Service handling all broker communication
 */
export class AmqService {
	
	private workerService: WorkerService;

	private payQueueDestination: string; 
	private ackQueueDestination: string; 
	private connectOptions: any;

	constructor(workerService: WorkerService) {
		this.workerService = workerService;
		this.payQueueDestination = process.env.AMQ_PAY_QUEUE_NAME!;
		this.ackQueueDestination = process.env.AMQ_ACK_QUEUE_NAME!;
		
		this.connectOptions = {
			'host': process.env.AMQ_BROKER_HOSTNAME!,
			'port': +process.env.AMQ_BROKER_PORT!,
			'connectHeaders':{
				'host': '/',
				'login': process.env.AMQ_USER_NAME,
				'passcode': process.env.AMQ_USER_PASS,
				'heart-beat': '5000:5000'
			}
		}
	}

	connectBroker() {
		const that = this;

		stompit.connect(this.connectOptions, function(error: any, client: any) {
			if (error) {
				console.log(' - amq connection error: ' + error.message);
				setTimeout(() => that.connectBroker(), 1000)
				return;
			}

			const sendHeaders = {
				'destination': that.ackQueueDestination,
				'content-type': 'text/plain'
			};
			
			const frame = client.send(sendHeaders);
			frame.write('acknowledge_startup');
			console.log("sending acknowledgement")
			frame.end()

			const subscribeHeaders = {
				'destination': that.payQueueDestination,
				'ack': 'client-individual'
			};
			
			client.subscribe(subscribeHeaders, function(error: any, message: any) {
				
				if (error) {
					console.log('subscribe error ' + error.message);
					setTimeout(() => that.connectBroker(), 1000)
					return;
				}
				
				message.readString('utf-8', function(error: any, body: any) {
				if (error) {
					console.log('read message error ' + error.message);
					return;
				}

				console.log("new message received");
				
				that.workerService.work(body);
				that.wait(3000)
				client.ack(message);
				// client.disconnect();
				});
			});
		});
	}

	wait(ms: any){
		var start = new Date().getTime();
		var end = start;
		while(end < start + ms) {
		  	end = new Date().getTime();
	   	}
	}
}