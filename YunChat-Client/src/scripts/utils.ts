import { ToastType } from "@/enums/ToastType"
import { ElMessage, ElMessageBox } from "element-plus"
import { useOnelDialogStore } from "@/stores/onelDialogStore"
import { useApplicationStore } from "@/stores/applicationStore"

const showToasts = async (type: string, text: string): Promise<void> => {
    if (type === ToastType.INFO) {
        ElMessage({ message: text, type: 'info', plain: true, grouping: true });
    } else if (type === ToastType.ERROR) {
        ElMessage({ message: text, type: 'error', plain: true, grouping: true });
    } else if (type === ToastType.SUCCESS) {
        ElMessage({ message: text, type: 'success', plain: true, grouping: true });
    } else if (type === ToastType.WARNING) {
        ElMessage({ message: text, type: 'warning', plain: true, grouping: true });
    }
}

const uploadFileError = (error: any): any => showToasts('error', error.message);
export default { showToasts, useApplicationStore, useOnelDialogStore, uploadFileError };