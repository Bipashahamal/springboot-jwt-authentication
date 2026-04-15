import json

def check_postman_json():
    try:
        with open('Employee_Management_API.postman_collection.json', 'r') as f:
            data = json.load(f)
        
        def process_items(items):
            for item in items:
                if 'item' in item:
                    process_items(item['item'])
                if 'request' in item and 'body' in item['request']:
                    body = item['request']['body']
                    if body.get('mode') == 'raw':
                        raw_content = body.get('raw', '')
                        try:
                            json.loads(raw_content)
                        except json.JSONDecodeError as e:
                            print(f"Error in request '{item['name']}': {e}")
                            print(f"Content: {raw_content}")

        process_items(data['item'])
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    check_postman_json()
