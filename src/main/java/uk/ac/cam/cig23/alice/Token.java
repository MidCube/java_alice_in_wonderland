/*
 * Copyright 2020 Andrew Rice <acr31@cam.ac.uk>, C.I. Griffiths
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.cig23.alice;

final class Token {
  private final String contents;
  private final String partOfSpeech;
  private final double confidence;

  Token(String contents, String partOfSpeech, double confidence) {
    this.contents = contents;
    this.partOfSpeech = partOfSpeech;
    this.confidence = confidence;
  }

  String contents() {
    return contents;
  }

  String partOfSpeech() {
    return partOfSpeech;
  }

  double confidence() {
    return confidence;
  }

  boolean isWord() {
    return contents().matches("\\w+");
  }

  @Override
  public String toString() {
    return String.format("%s(%s:%.1f)", contents(), partOfSpeech(), confidence());
  }
}
