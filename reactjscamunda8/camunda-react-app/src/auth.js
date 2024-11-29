import axios from 'axios';

const getAccessToken = async () => {
    const clientId = 'V__54JcdPlCAULUhS2DkUaI0OgrTbgGG';
    const clientSecret = '0OCJyKq.GQGa_H6h7-ioUeNUMvJpSHbbhLj6FJej4C0MvbgBC.fWA8ej_ghofJGS';
    const authUrl = 'https://login.cloud.camunda.io/oauth/token';

    try {
        const response = await axios.post(authUrl, {
            grant_type: 'client_credentials',
            client_id: clientId,
            client_secret: clientSecret,
            audience: 'operate.camunda.io',
        });

        return response.data.access_token;
    } catch (error) {
        console.error('Error fetching access token:', error);
        throw new Error('Authentication failed');
    }
};

export default getAccessToken;