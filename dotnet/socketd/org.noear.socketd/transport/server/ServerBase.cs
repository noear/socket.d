using org.noear.socketd.transport.core;

namespace org.noear.socketd.transport.server;

public abstract class ServerBase : IServer
{
    public string getTitle()
    {
        throw new NotImplementedException();
    }

    public ServerConfig getConfig()
    {
        throw new NotImplementedException();
    }

    public IServer config(IServerConfigHandler configHandler)
    {
        throw new NotImplementedException();
    }

    public IServer listen(IListener listener)
    {
        throw new NotImplementedException();
    }

    public IServer start()
    {
        throw new NotImplementedException();
    }

    public void stop()
    {
        throw new NotImplementedException();
    }
}