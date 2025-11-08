import {DefaultTheme, defineConfig} from 'vitepress'

export const ja = defineConfig({
    lang: "ja",
    themeConfig: {
        editLink: {
            pattern: "https://github.com/TRCDevelopers/Clayium/edit/develop/wiki/docs/:path",
            text: "GitHub で編集",
        },
        sidebar: sidebar()
    }
})

function sidebar(): DefaultTheme.SidebarItem[] {
    return [
        {
            text: "はじめに",
            base: "/introduction/",
            collapsed: false,
            items: [
                { text: "原作からの変更点", link: "changes-from-original" },
            ]
        },
        {
            text: "解説",
            base: "/features/",
            collapsed: true,
            items: [
                { text: "粘土レーザー", link: "clay-laser" },
                { text: "他MODとの連携", link: "mod-integrations" },
            ],
        },
        {
            text: "データ",
            base: "/data/",
            collapsed: true,
            items: [
                { text: "コンフィグ", link: "config" },
            ],
        },
        {
            text: "GroovyScript",
            base: "/groovy-script/",
            collapsed: true,
            items: [
                { text: "レシピ", link: "recipe" },
            ]
        },
    ]
}
