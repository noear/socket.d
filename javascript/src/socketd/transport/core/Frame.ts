import {Message} from "./Message";

export class Frame {
    constructor(flag: number, message?: Message) {
        this.flag = flag;
        this.message = message;
    }

    flag: number
    message?: Message
}
