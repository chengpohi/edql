package com.github.chengpohi.script

import com.github.chengpohi.context.HostInfo

case class EDQLRunContext(targetInstruction: String = null, runDir: String = "", hostInfo: HostInfo = null)