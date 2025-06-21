package de.uni_passau.fim.se2.sa.sign.interpretation;

import com.google.common.base.Preconditions;

public class SignTransferRelation implements TransferRelation {

  @Override
  public SignValue evaluate(final int pValue) {
    if (pValue < 0) {
      return SignValue.MINUS;
    } else if (pValue == 0) {
      return SignValue.ZERO;
    } else {
      return SignValue.PLUS;
    }
  }

  @Override
  public SignValue evaluate(final Operation pOperation, final SignValue pValue) {
    Preconditions.checkState(pOperation == Operation.NEG);
    Preconditions.checkNotNull(pValue);

    // we will handle negation here
    switch (pValue) {
      case PLUS: return SignValue.MINUS;
      case MINUS: return SignValue.PLUS;
      case ZERO: return SignValue.ZERO;
      case PLUS_MINUS: return SignValue.PLUS_MINUS;
      case ZERO_PLUS: return SignValue.ZERO_MINUS;
      case ZERO_MINUS: return SignValue.ZERO_PLUS;
      case BOTTOM: return SignValue.BOTTOM;
      default: return pValue;

    }
  }

  @Override
  public SignValue evaluate(
      final Operation pOperation, final SignValue pLHS, final SignValue pRHS) {
    Preconditions.checkState(
        pOperation == Operation.ADD
            || pOperation == Operation.SUB
            || pOperation == Operation.MUL
            || pOperation == Operation.DIV);
    Preconditions.checkNotNull(pLHS);
    Preconditions.checkNotNull(pRHS);

    switch (pOperation) {
      case ADD: return evaluateADD(pLHS,pRHS);
      case SUB: return evaluateSUB(pLHS,pRHS);
      case MUL: return evaluateMUL(pLHS,pRHS);
      case DIV: return evaluateDIV(pLHS,pRHS);
      default: return SignValue.TOP;
    }
  }

  public SignValue evaluateADD(final SignValue pLHS, final SignValue pRHS) {
      if (pLHS == SignValue.BOTTOM || pRHS == SignValue.BOTTOM) return SignValue.BOTTOM;
      if(pLHS == SignValue.TOP || pRHS == SignValue.TOP)  return SignValue.TOP;
      if(pLHS == SignValue.ZERO) return pRHS;
      if(pRHS == SignValue.ZERO) return pLHS;
      if (pLHS == SignValue.PLUS && pRHS == SignValue.PLUS) return SignValue.PLUS;
      if (pLHS == SignValue.MINUS && pRHS == SignValue.MINUS) return SignValue.MINUS;
      if (pLHS == SignValue.ZERO_MINUS && pRHS == SignValue.ZERO_MINUS) return SignValue.ZERO_MINUS;
      if (pLHS == SignValue.ZERO_PLUS && pRHS == SignValue.ZERO_PLUS) return SignValue.ZERO_PLUS;
      if (pLHS == SignValue.PLUS && pRHS == SignValue.ZERO_PLUS) return SignValue.PLUS;
      if (pLHS == SignValue.MINUS && pRHS == SignValue.ZERO_MINUS) return SignValue.MINUS;
    return SignValue.TOP;
  }
  public SignValue evaluateSUB (final SignValue pLHS, final SignValue pRHS) {
    SignValue negatedRHS = evaluate(Operation.NEG, pRHS);
    return evaluateADD(pLHS, negatedRHS);
  }

  public SignValue evaluateMUL (final SignValue pLHS, final SignValue pRHS) {
    if (pLHS == SignValue.TOP || pRHS == SignValue.TOP) return SignValue.TOP;

    if (pLHS == SignValue.ZERO || pRHS == SignValue.ZERO) return SignValue.ZERO;

    if (pLHS == SignValue.PLUS && pRHS == SignValue.PLUS) return SignValue.PLUS;
    if (pLHS == SignValue.MINUS && pRHS == SignValue.MINUS) return SignValue.PLUS;
    if (pLHS == SignValue.PLUS && pRHS == SignValue.MINUS) return SignValue.MINUS;
    if (pLHS == SignValue.MINUS && pRHS == SignValue.PLUS) return SignValue.MINUS;

    if ((pLHS == SignValue.PLUS && pRHS == SignValue.PLUS_MINUS) ||
            (pRHS == SignValue.PLUS && pLHS == SignValue.PLUS_MINUS))
      return SignValue.PLUS_MINUS;

    if ((pLHS == SignValue.MINUS && pRHS == SignValue.PLUS_MINUS) ||
            (pRHS == SignValue.MINUS && pLHS == SignValue.PLUS_MINUS))
      return SignValue.PLUS_MINUS;

    if ((pLHS == SignValue.ZERO_PLUS && pRHS == SignValue.ZERO_PLUS) ||
            (pLHS == SignValue.ZERO_MINUS && pRHS == SignValue.ZERO_MINUS))
      return SignValue.ZERO_PLUS;

    if (pLHS == SignValue.BOTTOM || pRHS == SignValue.BOTTOM) return SignValue.BOTTOM;

    return SignValue.TOP;

  }
  public SignValue evaluateDIV (final SignValue pLHS, final SignValue pRHS) {
    if (pLHS == SignValue.TOP || pRHS == SignValue.TOP) return SignValue.TOP;

    if (pRHS == SignValue.ZERO || pRHS == SignValue.ZERO_MINUS ||
            pRHS == SignValue.ZERO_PLUS || pRHS == SignValue.TOP)
      return SignValue.TOP;

    if (pLHS == SignValue.ZERO) return SignValue.ZERO;

    if (pLHS == SignValue.PLUS && pRHS == SignValue.PLUS) return SignValue.PLUS;
    if (pLHS == SignValue.MINUS && pRHS == SignValue.MINUS) return SignValue.PLUS;
    if (pLHS == SignValue.PLUS && pRHS == SignValue.MINUS) return SignValue.MINUS;
    if (pLHS == SignValue.MINUS && pRHS == SignValue.PLUS) return SignValue.MINUS;

    if (pLHS == SignValue.BOTTOM || pRHS == SignValue.BOTTOM) return SignValue.BOTTOM;

    return SignValue.TOP;
  }






}
