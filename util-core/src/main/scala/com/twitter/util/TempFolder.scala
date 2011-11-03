/*
 * Copyright 2010 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twitter.util

import java.io.File
import com.twitter.io.Files

/**
 * Test mixin that creates a new folder for a block of code to execute in.
 * The folder is recursively deleted after the test.
 *
 * Note, the [[com.twitter.util.io]] package would be a better home for this trait.
 */
trait TempFolder {
  private val _folderName = new ThreadLocal[File]

  def withTempFolder(f: => Any) {
    val tempFolder = System.getProperty("java.io.tmpdir")
    // Note: If we were willing to have a dependency on Guava in util-core
    // we could just use `com.google.common.io.Files.createTempDir()`
    var folder: File = null
    do {
      folder = new File(tempFolder, "scala-test-" + System.currentTimeMillis)
    } while (! folder.mkdir())
    _folderName.set(folder)

    try {
      f
    } finally {
      Files.delete(folder)
    }
  }

  def folderName = { _folderName.get.getPath }

  def canonicalFolderName = { _folderName.get.getCanonicalPath }
}
