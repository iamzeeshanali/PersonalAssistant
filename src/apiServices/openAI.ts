import OpenAI from "openai";

const client = new OpenAI({
    apiKey: ''
});

export async function interpret(text) {
  const res = await fetch("https://api.openai.com/v1/responses", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer`
    },
    body: JSON.stringify({
      model: "gpt-3.5-turbo",
      input: `
Extract intent and parameters.

User: "${text}"

Return JSON only.
`
    })
  });

  const json = await res.json();

  if (json.error) {
    console.log("OpenAI error:", json.error);
    throw json.error;
  }

  const output = json.output[0].content[0].text;

  return JSON.parse(output);
}



