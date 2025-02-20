import {OptionType} from "@/components/evaluation/draxo/OptionType";

export class Option {
  static YES = new Option("yes", "positive");
  static NO = new Option("no", "negative");
  static PARTIALLY = new Option("partially", "negative");
  static DONT_KNOW = new Option("dontKnow", "unknown");
  static NO_OPINION = new Option("noOpinion", "unknown");

  private constructor(private readonly i18nCode: string, public readonly cssClass: string) {
  }

  public label(): string {
    return `option.${this.i18nCode}`
  }

  public static get(type: OptionType | null): Option | null {
    if (type != null) {
      switch (type) {
        case OptionType.YES:
          return Option.YES;
        case OptionType.NO:
          return Option.NO;
        case OptionType.PARTIALLY:
          return Option.PARTIALLY;
        case OptionType.DONT_KNOW:
          return Option.DONT_KNOW;
        case OptionType.NO_OPINION:
          return Option.NO_OPINION;
      }
    }

    return null;
  }

  public static values(): Option[] {
    return [
      Option.YES,
      Option.NO,
      Option.PARTIALLY,
      Option.DONT_KNOW,
      Option.NO_OPINION
    ];
  }
}
