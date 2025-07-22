export default class StorageUtils {
    static saveLocalStorage = async (keys: string[], values: string[]) => {
        for (let index: number = 0; index < keys.length; index++) {
            localStorage.setItem(keys[index], values[index]);
        }
    }
    
    static removeLocalStorage = async (keys: stringp[]) => {
        for (let index: number = 0; index < keys.length; index++) {
            localStorage.removeItem(keys[index]);
        }
    }
}