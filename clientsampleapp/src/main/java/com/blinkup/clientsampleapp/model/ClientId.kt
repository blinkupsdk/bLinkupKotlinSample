package com.blinkup.clientsampleapp.model

data class ClientId(val name: String, val id: String)


object DemoClientIds {
    val clientIds = listOf(
        ClientId("Chicago Demo", "Ph_yH2e8JRpc0WBKiNNOYUYJs03kNEY3DXh7WIrXlJo="),
        ClientId("Milwaukee Bucks", "Ph1bFOq1moKmm0in2lxsfZ5v-No-Og6wWxEKM-6F1OM="),
        ClientId("Charlotte Hornets", "iqPbaubl_9FtQTTBGrueAdom0TnlSbTPZO675ZLQS1o="),
        ClientId("Atlanta Braves", "uzU20c9Zs6_-Sn3o_lv9jrPM4kZeH5nnnn05iNfc1FE="),
        ClientId("Clemson Tigers", "ssD1qVnNw1KFPT3eFFtquHiSo0qlZzcK783Kwku9xWU=")
    )
}
