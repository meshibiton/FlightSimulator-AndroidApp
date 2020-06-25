using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Net.Sockets;
using System.Text;

namespace FlightMobileApp.Models
{
    public class TcpTimeClient
    {
        private TcpClient client;
        NetworkStream stream;

        public TcpTimeClient()
        {

            client = new TcpClient();
            //set timeout for reading and writing
            client.SendTimeout = 100;
            client.ReceiveTimeout = 100;
        }
        

        public void Connect(string ip, int port)
        {
            //connect to socket
            try
            {
                client.Connect(ip, port);
                stream = client.GetStream();
                Write("data\n");

            }
            catch (Exception)
            {

                throw new Exception();
            }
        }
        public void Disconnect()
        {
            // Release the socket.    
            try
            {
                stream.Close();
                client.Close();
            }
            catch (Exception)
            {

                throw new Exception();

            }
        }

        public string Read()
        {
            string massage = "";
            try
            {
                byte[] bb = new byte[100];
                int k = stream.Read(bb, 0, 100);
                for (int i = 0; i < k; i++)
                    massage += (Convert.ToChar(bb[i]));
                Console.WriteLine("read " + massage);
            }

            catch (Exception)
            {
                throw new TimeoutException();
            }

            return massage;

        }



        public void Write(string command)
        {
            try
            {
                ASCIIEncoding asen = new ASCIIEncoding();
                byte[] ba = asen.GetBytes(command);
                stream.Write(ba, 0, ba.Length);
            }
            catch (Exception)
            {
                throw new TimeoutException();
            }
        }
    }
}
