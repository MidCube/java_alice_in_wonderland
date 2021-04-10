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

import static com.google.common.truth.Truth.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AliceTest {

  @Test
  public void countWords_returns0_forEmptyList() {
    // ARRANGE
    List<Token> words = List.of();

    // ACT
    long count = Alice.countWords(words);

    // ASSEERT
    assertThat(count).isEqualTo(0);
  }

  @Test
  public void countWords_returns0_whenOnlyPunctuation() {
    // ARRANGE
    List<Token> words = List.of(new Token(".", ".", 1.0), new Token(",", ",", 1.0));

    // ACT
    long count = Alice.countWords(words);

    // ASSEERT
    assertThat(count).isEqualTo(0);
  }

  @Test
  public void countWords_countsActualWords() {
    // ARRANGE
    List<Token> words = List.of(new Token("word", "NNP", 1.0), new Token("sup", "NNP", 1.0));

    // ACT
    long count = Alice.countWords(words);

    // ASSEERT
    assertThat(count).isEqualTo(2);
  }

  @Test
  public void vocabulary_ignoresCase() {
    // ARRANGE
    List<Token> words =
        List.of(
            new Token("Alice", "NNP", 1.0),
            new Token("alice", "NNP", 1.0),
            new Token("Queen", "NNP", 1.0),
            new Token("King", "NNP", 1.0),
            new Token("King", "NNP", 1.0));

    // ACT
    List<String> vocab = Alice.vocabulary(words, 2);

    // ASSERT
    assertThat(vocab).containsExactly("alice", "king");
  }

  @Test
  public void vocabulary_ignoresPunctuation() {
    // ARRANGE
    List<Token> words =
            List.of(
                    new Token("Alice", "NNP", 1.0),
                    new Token("King", "NNP", 1.0),
                    new Token(".", ".", 1.0));

    // ACT
    List<String> vocab = Alice.vocabulary(words, 3);

    // ASSERT
    assertThat(vocab).containsExactly("alice", "king");
  }


  @Test
  public void topN_returnsTopOne() {
    // ARRANGE
    Map<String, Long> frequencies = Map.of("pear", 5L, "banana", 1L, "apple", 10L);

    // ACT
    List<String> top = Alice.topN(1, frequencies);

    // ASSERT
    assertThat(top).containsExactly("apple");
  }

  @Test
  public void topN_returnsAll_ifNotEnoughPresent() {
    // ARRANGE
    Map<String, Long> frequencies = Map.of("apple", 10L, "pear", 5L, "banana", 1L);

    // ACT
    List<String> top = Alice.topN(10,frequencies);

    // ASSERT
    assertThat(top).containsExactly("apple", "pear", "banana");
  }

  // This test is not really useful but its here to make sure we get coverage of the Token class
  @Test
  public void tokenToString_returnsOneDecimalPlace() {
    // ARRANGE
    Token token = new Token("Alice", "NNP", 1.888);

    // ACT
    String string = token.toString();

    // ASSERT
    assertThat(string).isEqualTo(String.format("Alice(NNP:%.1f)", 1.9));
  }

  @Test
  public void properNouns_filtersNonProperNouns() {
    // ARRANGE
    List<Token> words =
            List.of(
                    new Token("Alice", "NNP", 1.0),
                    new Token("Queen", "NNP", 1.0),
                    new Token("King", "NNP", 1.0),
                    new Token("cool", "VVM", 1.0));

    // ACT
    List<String> nouns = Alice.properNouns(words,3);

    // ASSERT
    assertThat(nouns).containsExactly("Alice", "Queen", "King");
  }

  @Test
  public void properNouns_returnsAllNounsIfLessThanSize() {
    // ARRANGE
    List<Token> words =
            List.of(
                    new Token("Alice", "NNP", 1.0),
                    new Token("Queen", "NNP", 1.0),
                    new Token("King", "NNP", 1.0),
                    new Token("cool", "VVM", 1.0));

    // ACT
    List<String> nouns = Alice.properNouns(words,10);

    // ASSERT
    assertThat(nouns).containsExactly("Alice", "Queen", "King");
  }

  @Test
  public void properNouns_returnsEmptyListIfNoNouns() {
    // ARRANGE
    List<Token> words =
            List.of(
                    new Token("cool", "VVM", 1.0));

    // ACT
    List<String> nouns = Alice.properNouns(words,10);

    // ASSERT
    assertThat(nouns).isEmpty();
  }

  @Test
  public void leastConfidentToken_returnsNullIfEmpty() {
    // ARRANGE
    List<Token> words =
            List.of();

    // ACT
    Token leastConfident = Alice.leastConfidentToken(words);

    // ASSERT
    assertThat(leastConfident).isNull();
  }

  @Test
  public void leastConfidentToken_returnsLowestCertainty() {
    // ARRANGE
    Token word1 = new Token("Alice", "NNP", 1.0);
    Token word2 = new Token("Queen", "NNP", 0.1);
    Token word3 = new Token("King", "NNP", 1.0);
    List<Token> words =
            List.of(word1, word2, word3);

    // ACT
    Token leastConfident = Alice.leastConfidentToken(words);

    // ASSERT
    assertThat(leastConfident).isEqualTo(word2);
  }

  @Test
  public void leastConfidentToken_returnsFirstLowestCertaintyIfEqual() {
    // ARRANGE
    Token word1 = new Token("Alice", "NNP", 0.1);
    Token word2 = new Token("Queen", "NNP", 0.1);
    Token word3 = new Token("King", "NNP", 1.0);
    List<Token> words =
            List.of(word1, word2, word3);

    // ACT
    Token leastConfident = Alice.leastConfidentToken(words);

    // ASSERT
    assertThat(leastConfident).isEqualTo(word1);
  }

  @Test
  public void posFrequency_getsValidFrequencies() {
    // ARRANGE
    Map<String, Long> result = new HashMap<>();
    result.put("NNP",3L);
    result.put("VVM",1L);
    Token word1 = new Token("Alice", "NNP", 0.1);
    Token word2 = new Token("Queen", "NNP", 0.1);
    Token word3 = new Token("King", "NNP", 1.0);
    Token word4 = new Token("ya", "VVM", 1.0);
    List<Token> words =
            List.of(word1, word2, word3, word4);

    // ACT
    Map<String,Long> freq = Alice.posFrequencies(words);

    // ASSERT
    assertThat(freq).isEqualTo(result);
  }
}
