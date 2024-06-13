import type {Processor} from "../Processor";
import type {ChannelAssistant} from "../ChannelAssistant";
import type {StreamInternal} from "../../stream/Stream";
import type {Session} from "../Session";
import type {ChannelSupporter} from "../ChannelSupporter";
import type {Config} from "../Config";
import {Frame} from "../Frame";
import {MessageBuilder} from "../Message";
import {Constants} from "../Constants";
import {EntityMetas} from "../EntityMetas";
import {ChannelInternal} from "../Channel";
import {SessionDefault} from "../SessionDefault";
import type { IoBiConsumer } from "../Typealias";
import {SocketAddress} from "../SocketAddress";
import {ChannelBase} from "./ChannelBase";
import {Frames} from "./Frames";
import {StreamManger} from "../../stream/StreamManger";

export class ChannelDefault<S> extends ChannelBase implements ChannelInternal {
    private _source: S;
    //处理器
    private _processor: Processor;
    //助理
    private _assistant: ChannelAssistant<S>;
    //流管理器
    private _streamManger: StreamManger;
    //会话（懒加载）
    private _session: Session;
    //最后活动时间
    private _liveTime: number = 0;
    //告警代号
    private _alarmCode: number = 0;
    //打开前景（用于构建 onOpen 异步处理）
    private _onOpenFuture: IoBiConsumer<boolean, Error>;
    //关闭代号（用于做关闭异常提醒）//可能协议关；可能用户关
    private _closeCode: number = 0;

    constructor(source: S, supporter: ChannelSupporter<S>) {
        super(supporter.getConfig());
        this._source = source;
        this._processor = supporter.getProcessor();
        this._assistant = supporter.getAssistant();
        this._streamManger = supporter.getConfig().getStreamManger();
    }

    onOpenFuture(future: IoBiConsumer<boolean, Error>) {
        this._onOpenFuture = future;
    }

    doOpenFuture(r: boolean, e: Error) {
        if (this._onOpenFuture) {
            this._onOpenFuture(r, e);
        }
    }

    isValid(): boolean {
        return this.closeCode() == 0 && this._assistant.isValid(this._source);
    }


    isClosing(): boolean {
        return this._closeCode == Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING;
    }

    closeCode(): number {
        if (this._closeCode > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            return this._closeCode;
        } else {
            return 0;
        }
    }

    config(): Config {
        return this._config;
    }

    sendPing() {
        this.send(Frames.pingFrame(), null);
    }

    sendPong() {
        this.send(Frames.pongFrame(), null);
    }

    getRemoteAddress(): SocketAddress | null {
        return this._assistant.getRemoteAddress(this._source);
    }

    getLocalAddress(): SocketAddress | null {
        return this._assistant.getLocalAddress(this._source);
    }

    send(frame: Frame, stream: StreamInternal<any> | null) {
        if (this.getConfig().clientMode()) {
            //console.debug("C-SEN:" + frame);
        } else {
            //只打印服务端的（客户端的容易被人看光）
            console.debug("S-SEN:" + frame);
        }


        if (frame.message()) {
            const message = frame.message()!;

            //注册流接收器
            if (stream != null) {
                this._streamManger.addStream(message.sid(), stream);
            }

            //如果有实体（尝试分片）
            if (message.entity() != null) {
                //确保用完自动关闭

                if (message.dataSize() > this.getConfig().getFragmentSize()) {
                    message.putMeta(EntityMetas.META_DATA_LENGTH, message.dataSize().toString());
                }

                this.getConfig().getFragmentHandler().spliFragment(this, stream, message, fragmentEntity => {
                    //主要是 sid 和 entity
                    const fragmentFrame = new Frame(frame.flag(), new MessageBuilder()
                        .flag(frame.flag())
                        .sid(message.sid())
                        .event(message.event())
                        .entity(fragmentEntity)
                        .build());

                    this._processor.sendFrame(this, fragmentFrame, this._assistant, this._source);
                });
                return;
            }
        }

        //不满足分片条件，直接发
        this._processor.sendFrame(this, frame, this._assistant, this._source);
        if (stream != null) {
            stream.onProgress(true, 1, 1);
        }
    }

    reconnect() {
        //由 ClientChannel 实现
    }

    onError(error: Error) {
        this._processor.onError(this, error);
    }

    getLiveTime(): number {
        return this._liveTime;
    }

    setLiveTimeAsNow() {
        this._liveTime = new Date().getTime();
    }

    setAlarmCode(alarmCode: number){
        this._alarmCode = alarmCode;
    }

    getSession(): Session {
        if (this._session == null) {
            this._session = new SessionDefault(this);
        }

        return this._session;
    }

    getStream(sid: string): StreamInternal<any> | null {
        return this._streamManger.getStream(sid);
    }

    setSession(session: Session) {
        this._session = session;
    }

    close(code) {
        try {
            this._closeCode = code;

            super.close(code);

            if (code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
                if (this._assistant.isValid(this._source)) {
                    //如果有效且非预关闭，则尝试关闭源 //外面的 sendClose 是异步的，所以晚会儿关闭
                    setTimeout(() => {
                        this._assistant.close(this._source);
                    }, 100);

                    console.debug(`${this.getConfig().getRoleName()} channel closed, sessionId=${this.getSession().sessionId()}`);
                }
            }
        } catch (e) {
            console.warn(`${this.getConfig().getRoleName()} channel close error, sessionId=${this.getSession().sessionId()}`, e);
        }

        if (code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            this.onCloseDo();
        }
    }
    private _isCloseNotified:boolean = false;
    private onCloseDo(){
        if (this._isCloseNotified == false) {
            this._isCloseNotified = true;
            this._processor.doCloseNotice(this);
        }
    }
}