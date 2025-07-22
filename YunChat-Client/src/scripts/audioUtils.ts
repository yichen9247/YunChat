export default class audioUtils {
    static playNoticeVoice = async (): Promise<void> => {
        const audioType = localStorage.getItem('audio') || 'default';
        let audioRef = document.getElementById("audioRef") as HTMLAudioElement;
        if (!audioRef) {
            audioRef = document.createElement("audio");
            audioRef.id = "audioRef";
            document.body.appendChild(audioRef);
        }
        let audioPath: string;
        switch (audioType) {
            case 'apple':
                audioPath = (await import('@/assets/audio/apple.mp3')).default;
                break;
            case 'momo':
                audioPath = (await import('@/assets/audio/momo.mp3')).default;
                break;
            case 'huaji':
                audioPath = (await import('@/assets/audio/huaji.mp3')).default;
                break;
            default:
                localStorage.setItem('audio', 'default');
                audioPath = (await import('@/assets/audio/default.mp3')).default;
                break;
        }
        audioRef.src = audioPath;
        try {
            await audioRef.play();
        } catch (err) {
            console.error("播放音频失败:", err);
        }
    }
}