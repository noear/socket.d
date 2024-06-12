import {EntityMetas} from "../EntityMetas";
import {Entity} from "../Entity";
import {EntityDefault} from "./EntityDefault";


export class FileEntity extends EntityDefault implements Entity {

    constructor(file: File) {
        super();
        this.dataSet(file);
        this.metaPut(EntityMetas.META_DATA_DISPOSITION_FILENAME, file.name);
    }
}