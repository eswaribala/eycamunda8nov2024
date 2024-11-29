import { Camunda8 } from "@camunda8/sdk";
import path from "path"; // we'll use this later
import dotenv from 'dotenv';
// Load environment variables from the .env file
dotenv.config();
const camunda = new Camunda8({
    ZEEBE_ADDRESS:process.env.ZEEBE_ADDRESS,
    CAMUNDA_CONSOLE_CLIENT_ID:process.env.ZEEBE_CLIENT_ID,
    CAMUNDA_CONSOLE_CLIENT_SECRET:process.env.ZEEBE_CLIENT_SECRET,
    CAMUNDA_OAUTH_URL:process.env.CAMUNDA_OAUTH_URL

});

//const camunda = new Camunda8();
const zeebe = camunda.getZeebeGrpcApiClient();
const operate = camunda.getOperateApiClient();
const tasklist = camunda.getTasklistApiClient();
async function main() {
    const deploy = await zeebe.deployResource({
        processFilename: path.join(process.cwd(), "callprocess.bpmn"),
    });
    console.log(
        `[Zeebe] Deployed process ${deploy.deployments[0].process.bpmnProcessId}`
    );
}

main(); // remember to invoke the function