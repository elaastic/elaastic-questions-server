export class Criteria {
  static D = new Criteria("understandable", "D");
  static R = new Criteria("relevant", "R");
  static A = new Criteria("agreed", "A");
  static X = new Criteria("exhaustive", "X");
  static O = new Criteria("optimal", "O");

  constructor(public readonly i18nCode: string, public readonly capitalLetter: string) {
  }

  public header(): string {
    return `criteria.${this.i18nCode}.header`;
  }

  public question(): string {
    return `criteria.${this.i18nCode}.question`;
  }

  public static values(): Criteria[] {
    return [
      Criteria.D,
      Criteria.R,
      Criteria.A,
      Criteria.X,
      Criteria.O
    ];
  }

  public equals(other: any): boolean {
    if (this === other) return true;
    if (other === null || other === undefined) return false;
    if (this.constructor !== other.constructor) return false;

    const otherCriteria = other as Criteria;
    return this.i18nCode === otherCriteria.i18nCode;
  }
}
