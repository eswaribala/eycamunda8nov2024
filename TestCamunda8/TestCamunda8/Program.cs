// See https://aka.ms/new-console-template for more information
using System;
using System.Net.Http;
using System.Threading.Tasks;
using Zeebe.Client;
using Zeebe.Client.Impl.Builder;
var zeebeClient = CamundaCloudClientBuilder.Builder()
            .UseClientId("V__54JcdPlCAULUhS2DkUaI0OgrTbgGG")          // Replace with your Camunda Cloud Client ID
            .UseClientSecret("0OCJyKq.GQGa_H6h7-ioUeNUMvJpSHbbhLj6FJej4C0MvbgBC.fWA8ej_ghofJGS")  // Replace with your Camunda Cloud Client Secret
            
            .UseContactPoint("7952082d-4604-4a35-b248-58fc9df57155.sin-1.zeebe.camunda.io:443")
            .Build();

// Deploy a process
var deployResponse = await zeebeClient.NewDeployCommand()
    .AddResourceFile("F:\\Local disk\\dotnetcamundams\\TestCamunda8\\TestCamunda8\\callprocess.bpmn")
    .Send();

Console.WriteLine($"Deployed process: {deployResponse.Processes[0].BpmnProcessId}");
