from .editors import StyleTransfer, Config


def getStyleTransfer(content_path, style_path, output_path):
    config = Config('rest/editors/configs/transfer.yaml')
    styler = StyleTransfer(content_path, style_path, output_path, config)
    styler.run_bulk()
    print(f'Image style-transfered at {output_path}')




