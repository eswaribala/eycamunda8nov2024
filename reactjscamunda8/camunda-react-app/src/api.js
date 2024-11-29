import axios from 'axios';
import getAccessToken from './auth';

const camundaApiBaseURL = 'https://sin-1.operate.camunda.io:443/7952082d-4604-4a35-b248-58fc9df57155/v1';

const startProcessInstance = async (processDefinitionKey, variables) => {
    const token = await getAccessToken();
     console.log(token)
    try {
        const response = await axios.post(
            `${camundaApiBaseURL}/process-definitions/search`,
            {
                processDefinitionKey,
                variables,
            },
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            }
        );

        console.log('Process instance started:', response.data);
        return response.data;
    } catch (error) {
        console.error('Error starting process instance:', error.response?.data || error.message);
        throw error;
    }
};

export { startProcessInstance };
