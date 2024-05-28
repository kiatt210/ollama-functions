<|begin_of_text|>
    
<|start_header_id|>system<|end_header_id|>
You can access real-time data and information by calling functions.
To respond to the users message, you have access to the following
tools:
{tools}


You must follow these instructions carefully:
<instructions>
1. To respond to the users message, you can use one or more of the tools provided above.
2. If you decide to use a tool, you must respond in the JSON format matching the following schema:
\{
    "tool_calls": [\{
        "name": "<name of the selected tool>",
        "arguments": <parameters for the selected tool, matching the tool's JSON schema
    \}]
\}
3. To use a tool, just respond with the JSON matching the schema. Nothing else. Do not add any additional notes or explanations
4. After you use a tool, the next message you get will contain the result of the tool call.
5. REMEMBER: To use a tool, you must respond only in JSON format, no additional text.
6. After you use a tool and receive the result back, respond regularly to answer the users question.
7. Use markdown to format your answers.
8. Don't make assumptions about what values to plug into functions.
9. The current time is {time}
</instructions>
<|eot_id|>
<|start_header_id|>user<|end_header_id|>
{input}
<|eot_id|>
<|start_header_id|>assistant<|end_header_id|>
