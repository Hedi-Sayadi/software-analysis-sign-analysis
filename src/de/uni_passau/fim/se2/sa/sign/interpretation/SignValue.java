package de.uni_passau.fim.se2.sa.sign.interpretation;

import com.google.common.base.Preconditions;
import org.objectweb.asm.tree.analysis.Value;

/** An enum representing the possible abstract values of the sign analysis. */
public enum SignValue implements Value {
  // Important! This implementation is kind of fragile. Don't change the order of enum values!
  // Otherwise, code will break 🤡:)
  BOTTOM("⊥"), // 0
  MINUS("{–}"), // 1
  ZERO("{0}"), // 2
  ZERO_MINUS("{0,–}"), // 3 == ZERO | MINUS
  PLUS("{+}"), // 4 == 4
  PLUS_MINUS("{+,–}"), // 5 == PLUS | MINUS
  ZERO_PLUS("{0,+}"), // 6 == ZERO | PLUS
  TOP("⊤"), // 7 == MINUS | ZERO | PLUS
  UNINITIALIZED_VALUE("∅"); // 8

  private final String repr;

  SignValue(final String pRepr) {
    repr = pRepr;
  }

  @Override
  public int getSize() {
    return 1;
  }

  @Override
  public String toString() {
    return repr;
  }

  public SignValue join(final SignValue pOther) {
    Preconditions.checkState(
        this != UNINITIALIZED_VALUE && pOther != UNINITIALIZED_VALUE,
        "Dummy shall not be used as a value.");

    if (this == BOTTOM) return pOther;
    if (pOther == BOTTOM) return this;
    if (this == pOther) return this;
    if (this == TOP || pOther == TOP) return TOP;

    switch (this) {
      case MINUS:
        switch (pOther) {
          case ZERO: return ZERO_MINUS;
          case ZERO_MINUS: return ZERO_MINUS;
          case PLUS: return PLUS_MINUS;
          case PLUS_MINUS: return PLUS_MINUS;
          case ZERO_PLUS: return TOP;
          default:return TOP;
        }
      case ZERO:
        switch (pOther) {
          case MINUS: return ZERO_MINUS;
          case ZERO_MINUS: return ZERO_MINUS;
          case PLUS: return ZERO_PLUS;
          case PLUS_MINUS: return TOP;
          case ZERO_PLUS: return ZERO_PLUS;
          default: return TOP;
        }
      case ZERO_MINUS:
        switch (pOther) {
          case MINUS: return ZERO_MINUS;
          case ZERO: return ZERO_MINUS;
          case PLUS: return TOP;
          case PLUS_MINUS: return TOP;
          case ZERO_PLUS: return TOP;
          default: return TOP;
        }
      case PLUS:
        switch (pOther) {
          case MINUS: return PLUS_MINUS;
          case ZERO: return ZERO_PLUS;
          case ZERO_MINUS: return TOP;
          case PLUS_MINUS: return PLUS_MINUS;
          case ZERO_PLUS: return ZERO_PLUS;
          default: return TOP;
        }
      case PLUS_MINUS:
        switch (pOther) {
          case MINUS: return PLUS_MINUS;
          case ZERO: return TOP;
          case ZERO_MINUS: return TOP;
          case PLUS: return PLUS_MINUS;
          case ZERO_PLUS: return TOP;
          default: return TOP;
        }
      case ZERO_PLUS:
        switch (pOther) {
          case MINUS: return TOP;
          case ZERO: return ZERO_PLUS;
          case ZERO_MINUS: return TOP;
          case PLUS: return ZERO_PLUS;
          case PLUS_MINUS: return TOP;
          default:return TOP;
        }
      default:
        return TOP;
    }
  }

  public boolean isLessOrEqual(final SignValue pOther) {
    Preconditions.checkState(
        this != UNINITIALIZED_VALUE && pOther != UNINITIALIZED_VALUE,
        "Dummy shall not be used as a value.");

    if (this == BOTTOM) return true;
    if (pOther == BOTTOM) return false;
    if (pOther == TOP) return true;
    if (this == TOP) return false;
    if (this == pOther) return true;
    if (this == MINUS) return pOther == ZERO_MINUS || pOther == PLUS_MINUS;
    if (this == ZERO) return pOther == ZERO_MINUS || pOther == ZERO_PLUS;
    if (this == PLUS) return pOther == ZERO_PLUS || pOther == PLUS_MINUS;
    return false;

  }

  public static boolean isZero(final SignValue pValue) {
    return pValue == ZERO;
  }

  public static boolean isMaybeZero(final SignValue pValue) {
    return pValue == ZERO_MINUS || pValue == ZERO_PLUS || pValue == TOP;
  }

  public static boolean isNegative(final SignValue pValue) {
    return pValue == MINUS;
  }

  public static boolean isMaybeNegative(final SignValue pValue) {
    return pValue == ZERO_MINUS || pValue == PLUS_MINUS || pValue == TOP;
  }
}
