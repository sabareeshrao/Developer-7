#!/usr/bin/env python3
"""Compile a source-faithful, questions-only archive; do not change learning status."""
from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TRACKERS = [
    ROOT / "state/LEARNING_TRACKER.md",
    ROOT / "state/LEARNING_TRACKER_SETS_076_080.md",
    ROOT / "state/LEARNING_TRACKER_SETS_081_085.md",
]
PREVIEWS = [
    ROOT / "docs/questions/previews/SETS_086_095.md",
    ROOT / "docs/questions/previews/SETS_096_105.md",
]
OUTPUT = ROOT / "docs/questions/ALL_QUESTIONS_SETS_001_105.md"

SET_HEADING = re.compile(
    r"^(?:# Set (\d+)\s*$|## Set (\d+)\s+—[^\n]*$)",
    re.MULTILINE,
)
ANCHOR = re.compile(
    r"^- \[[ x]\] (⭐{1,2})\s+(?:\[Master \d+\]\s*)?(.+)$"
)
QUESTION = re.compile(
    r"^- \[[ x]\]\s*(✅|💡)?\s*(?:\[Master \d+\]\s*)?(.+)$"
)
PART = re.compile(r"^#{2,3} Part ([A-Z])\s*$")


def parse_completed() -> dict[int, tuple[str, str, list[tuple[str, list[str]]]]]:
    sets = {}
    for source in TRACKERS:
        text = source.read_text(encoding="utf-8")
        headings = list(SET_HEADING.finditer(text))
        for index, match in enumerate(headings):
            number = int(match.group(1) or match.group(2))
            if not 1 <= number <= 85 or number in sets:
                continue
            section = text[match.start():headings[index + 1].start()
                           if index + 1 < len(headings) else len(text)]
            anchor = None
            parts = []
            active = None
            for line in section.splitlines():
                found = ANCHOR.match(line)
                if found and anchor is None:
                    anchor = (found.group(1), found.group(2))
                    continue
                found = PART.match(line)
                if found:
                    active = (found.group(1), [])
                    parts.append(active)
                    continue
                found = QUESTION.match(line)
                if found and active is not None:
                    # Stop at evidence headings, not at the next part heading.
                    marker = found.group(1) or "🆕"
                    question = found.group(2).strip()
                    active[1].append(f"{marker} {question}")
                if line.startswith("## Set ") or line.startswith(
                        "## Set ") or line.startswith("### Evidence"):
                    active = None
            if anchor is None or not parts or any(not part[1] for part in parts):
                raise ValueError(f"Missing anchor/questions for Set {number} in {source}")
            sets[number] = (anchor[0], anchor[1], parts)

    expected = set(range(1, 86))
    if set(sets) != expected:
        raise ValueError(f"Completed Set coverage mismatch: {sorted(expected - set(sets))}")
    return sets


def build() -> str:
    sets = parse_completed()
    result = [
        "# Developer-7 — All Interview Questions (Sets 1–105)",
        "",
        "**Status:** Sets **1–85 completed** in Developer-7; "
        "Sets **86–105 are questions-only previews**, not completed.",
        "This document does not change progress from **85/387+**.",
        "",
        "**Markers:** ⭐ original experience question · ⭐⭐ synthetic experience question · "
        "🆕 new technical question · ✅ previously covered technical question reused · "
        "💡 proposed synthetic technical question.",
        "",
        "---",
        "",
        "# Completed Sets 1–85",
        "",
    ]
    for number in range(1, 86):
        symbol, anchor, parts = sets[number]
        result.extend([
            f"## Set {number} — Status: {number}/387+",
            "",
            f"{symbol} **{anchor}**",
            "",
        ])
        for letter, questions in parts:
            result.extend([f"### Part {letter}", ""])
            result.extend(f"{idx}. {question}" for idx, question in
                          enumerate(questions, 1))
            result.append("")
        result.extend(["---", ""])

    result.extend([
        "# Future Sets 86–105 (questions only, not completed)",
        "",
    ])
    for file in PREVIEWS:
        result.append(file.read_text(encoding="utf-8").strip())
        result.extend(["", "---", ""])

    output = "\n".join(result).strip() + "\n"
    numbers = [int(num) for num in re.findall(
        r"^## Set (\d+)\s+—", output, flags=re.MULTILINE
    )]
    if numbers != list(range(1, 106)):
        raise ValueError(f"Expected 105 ordered Sets, got {numbers}")
    question_lines = re.findall(
        r"^\d+\. (?:🆕|✅|💡) .+$", output, flags=re.MULTILINE
    )
    if len(question_lines) < 650:
        raise ValueError(f"Question archive unexpectedly short: {len(question_lines)}")
    print(f"Verified: {len(numbers)} Sets; {len(question_lines)} technical-question entries")
    return output


def main() -> None:
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text(build(), encoding="utf-8")
    print(f"Wrote {OUTPUT.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
