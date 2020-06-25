using FlightMobileApp.Models;
using System.Collections.Generic;
using System.Drawing;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
    public interface ICommandManager
    {
        public Image GetScreenshotFromSim();
        public void SendCommandToSim(Command command);
        //public void DeleteFlight(string id);
    }
}