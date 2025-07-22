export interface EncryptService {
    encryptLogin(username: string, password: string): Promise<string>
    encryptAuthorization(): Promise<string>;
}