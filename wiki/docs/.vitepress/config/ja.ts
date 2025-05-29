import {DefaultTheme, defineConfig} from 'vitepress'

export const ja = defineConfig({
    lang: "ja",
    themeConfig: {
        editLink: {
            pattern: "https://github.com/bqc0n/clayium-wiki/edit/main/docs/:path",
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
