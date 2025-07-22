<script setup lang="ts">
    import Swal from "sweetalert2"
    import utils from "@/scripts/utils"
    
    const applicationStore = utils.useApplicationStore();

    // Categorized emoji list for better organization
    const emojeList: string[] = [
        // Smileys & Emotion
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍",
        "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩", "😏",
        
        // Negative Emotions  
        "😒", "😞", "😔", "😟", "😕", "🙁", "😣", "😖", "😫", "😩", "😢", "😭", "😮", "💨", "😤",
        "😠", "😡", "🤬", "🤯", "😳", "😱", "😨", "😰", "😥", "😓",
        
        // Face Actions
        "🤗", "🤔", "🤭", "🤫", "🤥", "😶", "🌫️", "😐", "😑", "😬", "🙄", "😯", "😦", "😧", "😮",
        "😲", "😴", "🤤", "😪", "😵", "💫", "🤐", "🤢", "🤮", "🤧",
        
        // Special Characters
        "😷", "🤒", "🤕", "🤑", "🤠", "😈", "👿", "👹", "👺", "🤡", "💩", "👻", "💀", "👽", "👾",
        "🤖", "🎃", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾", "🐶"
    ]

    /**
     * Insert selected emoji into chat input
     * @param {string} emoje - Selected emoji character
     */
    const renderEmoje = (emoje: string): void => {
        Swal.close();
        applicationStore.chantInput += emoje;
    }
</script>

<template>
    <div class="emoje-list">
        <span 
            v-for="(item, index) in emojeList" 
            :key="index"
            class="emoje-item" 
            @click="renderEmoje(item)"
        >
            {{ item }}
        </span>
    </div>
</template>

<style lang="less">
    @import url("../../styles/dialog/CenterDialog/EmojeCenter.less");
</style>