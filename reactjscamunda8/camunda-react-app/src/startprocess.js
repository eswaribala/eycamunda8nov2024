import React, { useState } from 'react';
import { startProcessInstance } from './api';

const StartProcess = () => {
    const [processKey, setProcessKey] = useState('');
    const [variables, setVariables] = useState('');
    const [response, setResponse] = useState(null);

    const handleStartProcess = async () => {
        try {
            const variablesObject = JSON.parse(variables); // Ensure variables are in JSON format
            const result = await startProcessInstance(processKey, variablesObject);
            setResponse(result);
        } catch (error) {
            console.error('Failed to start process instance:', error);
            setResponse('Error starting process instance');
        }
    };

    return (
        <div>
            <h2>Start Process Instance</h2>
            <input
                type="text"
                placeholder="Process Definition Key"
                value={processKey}
                onChange={(e) => setProcessKey(e.target.value)}
            />
            <textarea
                placeholder="Variables (JSON format)"
                value={variables}
                onChange={(e) => setVariables(e.target.value)}
            />
            <button onClick={handleStartProcess}>Start Process</button>

            {response && (
                <div>
                    <h3>Response</h3>
                    <pre>{JSON.stringify(response, null, 2)}</pre>
                </div>
            )}
        </div>
    );
};

export default StartProcess;
