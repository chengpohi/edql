package com.github.chengpohi.dsl.edql

trait QueryEDQL extends EDQLDefinition with IndexEDQL {

  case object get {
  }

  case object search {
  }

  case object bulk {
  }

  case object update {
  }

  case object dump {
  }

  case object reindex {
  }
}
