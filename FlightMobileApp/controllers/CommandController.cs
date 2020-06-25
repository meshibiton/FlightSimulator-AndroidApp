using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FlightMobileApp.Models;
using Microsoft.AspNetCore.Mvc;
using System.Drawing;

namespace FlightMobileApp.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class CommandController : ControllerBase
    {
        private readonly ICommandManager commandManager;
        public CommandController(ICommandManager command)
        {
            this.commandManager = command;
        }

        // GET: screenshot
        [HttpGet(Name = "GetScreenshot")]
        [Consumes("application/json")]
        public async Task<ActionResult> GetScreenshot(string id)
        {
            
            try
            {
                commandManager.GetScreenshotFromSim();
                return await Task.FromResult(StatusCode(200));
            }
            catch
            {
                //InternalServerErrorResult
                return await Task.FromResult(StatusCode(500));
            }
        }

        // POST: api/command
        [HttpPost(Name = "SendCommand")]
        [Consumes("application/json")]
        public async Task<ActionResult> SendCommand (Command c)
        {
            try
            {
                commandManager.SendCommandToSim(c);
                return await Task.FromResult(StatusCode(200));
            }
            catch
            {
                //InternalServerErrorResult
                return await Task.FromResult(StatusCode(200));
            }
        }
    }
}
