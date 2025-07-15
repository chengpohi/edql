package com.github.chengpohi.script

import com.github.chengpohi.context.HostInfo

data class EDQLRunContext(
    val targetInstruction: String? = null,
    val filePath: String = "",
    val runDir: String = "",
    val hostInfo: HostInfo
) 