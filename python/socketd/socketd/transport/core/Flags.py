
class Flags:
    Unknown = 0
    Connect = 10
    Connack = 11
    Ping = 20
    Pong = 21
    Close = 30
    Alarm = 31
    Pressure = 32
    Message = 40
    Request = 41
    Subscribe = 42
    Reply = 48
    ReplyEnd = 49

    @staticmethod
    def of(code):
        if code == 10:
            return Flags.Connect
        elif code == 11:
            return Flags.Connack
        elif code == 20:
            return Flags.Ping
        elif code == 21:
            return Flags.Pong
        elif code == 30:
            return Flags.Close
        elif code == 31:
            return Flags.Alarm
        elif code == 32:
            return Flags.Pressure
        elif code == 40:
            return Flags.Message
        elif code == 41:
            return Flags.Request
        elif code == 42:
            return Flags.Subscribe
        elif code == 48:
            return Flags.Reply
        elif code == 49:
            return Flags.ReplyEnd
        else:
            return Flags.Unknown

    @staticmethod
    def name(code):
        if code == 10:
            return "Connect"
        elif code == 11:
            return "Connack"
        elif code == 20:
            return "Ping"
        elif code == 21:
            return "Pong"
        elif code == 30:
            return "Close"
        elif code == 31:
            return "Alarm"
        elif code == 32:
            return "Pressure"
        elif code == 40:
            return "Message"
        elif code == 41:
            return "Request"
        elif code == 42:
            return "Subscribe"
        elif code == 48:
            return "Reply"
        elif code == 49:
            return "ReplyEnd"
        else:
            return "Unknown"

