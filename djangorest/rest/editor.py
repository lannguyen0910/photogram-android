from .editors import StyleTransfer, Config

DEFAULT_STYLE_DIR = 'rest/editors/style_transfer/examples/style'

def getStyleTransfer(content_path, style_path, output_path):
    config = Config('rest/editors/configs/transfer.yaml')
    styler = StyleTransfer(content_path, style_path, output_path, config)
    styler.run_bulk()
    print(f'Image style-transfered at {output_path}')




