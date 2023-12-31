"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.GuidGenerator = void 0;
const StrUtils_1 = require("../../utils/StrUtils");
class GuidGenerator {
    generate() {
        return StrUtils_1.StrUtils.guid();
    }
}
exports.GuidGenerator = GuidGenerator;
