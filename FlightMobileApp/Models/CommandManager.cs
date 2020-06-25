using System;
using System.Drawing;

namespace FlightMobileApp.Models
{
    public class CommandManager : ICommandManager
    {
        private readonly TcpTimeClient tcpClient;
        private readonly string ip;
        private readonly int port;
        private readonly int internalSimPort;

        //private readonly IExternalFlight externalFlight;
        public CommandManager()
        {
            tcpClient = new TcpTimeClient();
            ip = "127.0.0.1";
            try
            {
                port = 5401;
            } 
            catch
            {
                throw new Exception();
            }
            internalSimPort = 5402;
            //tcpClient.Connect(ip, port);
            //tcpClient.Write("data\n");
            
        }
        public Image GetScreenshotFromSim()
        {
            try
            {
                Image screenShot = Image.FromFile("C:/Users/user/source/repos/FlightMobileApp/FlightMobileApp/Models/plane.jpg");
                return screenShot;
               // tcpClient.Write("Get " + ip + ":" + internalSimPort + "/screenshot \n");
               //

            } 
            catch
            {

                throw new Exception();
            }
        }
        public void SendCommandToSim(Command command)
        {
            if (command == null) 
            {
                throw new Exception();
         

            }
            try
            {
                //tcpClient.Write("set/controls/flight/aileron" + command.Aileron + "\n");
                //tcpClient.Write("set/controls/flight/rudder" + command.Rudder + "\n");
                //tcpClient.Write("set/controls/flight/elevator" + command.Elevator + "\n");
                //tcpClient.Write("set/controls/flight/throttle" + command.Throttle + "\n");
            }
            catch
            {
                throw new Exception();
            }
        }
    }
}
