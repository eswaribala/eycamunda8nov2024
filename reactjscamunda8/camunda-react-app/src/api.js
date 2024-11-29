import axios from 'axios';
import getAccessToken from './auth';

const camundaApiBaseURL = 'https://sin-1.zeebe.camunda.io/7952082d-4604-4a35-b248-58fc9df57155/v1';

const startProcessInstance = async (processDefinitionKey) => {
    const token = await getAccessToken();

    try {
        const response = await axios.post(
            `${camundaApiBaseURL}/process-instances`,
            {
                processDefinitionKey
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
