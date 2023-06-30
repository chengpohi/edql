package com.github.chengpohi.script

import com.github.chengpohi.context.HostInfo

case class EDQLRunContext(targetInstruction: Option[String] = None, runDir: String = "", hostInfo: HostInfo = null)