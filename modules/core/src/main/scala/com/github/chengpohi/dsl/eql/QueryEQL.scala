package com.github.chengpohi.dsl.eql

trait QueryEQL extends EQLDefinition with IndexEQL {

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
